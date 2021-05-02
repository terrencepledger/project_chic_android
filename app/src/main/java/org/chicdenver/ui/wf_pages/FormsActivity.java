package org.chicdenver.ui.wf_pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.chicdenver.R;
import org.chicdenver.data.model.ChicAssignments.CPActivityAdapter;
import org.chicdenver.data.model.ChicAssignments.ChicCourse;
import org.chicdenver.data.model.Documents.DocumentAdapter;
import org.chicdenver.data.model.Documents.WfDoc;
import org.chicdenver.ui.DrawerActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static org.chicdenver.ui.main_pages.AccountActivity.copyStream;

public class FormsActivity extends DrawerActivity {

    String path;
    List<WfDoc> wfDocs = new ArrayList<>();
    RecyclerView formsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        activity = this;

        formsRecycler = findViewById(R.id.formRecycler);

        init();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode == Activity.RESULT_OK) {
            File docFile = null;
            try {
                docFile = createDocFile();
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(docFile);
                // Copying
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();

                String path = String.format("docs/%s", loggedInUser.getUid());
                StorageReference ref = FirebaseStorage.getInstance().getReference().child(path);

                String imagePath = getRealPathFromUri(data.getData());
                String origName = new File(imagePath).getName();
                List<String> split = new LinkedList<>(Arrays.asList(origName.split("\\.")));
                String ending = split.remove(split.size() - 1);
                String fileName = TextUtils.join("", split);

                ref.child(fileName + "." + ending)
                        .putFile(Uri.fromFile(docFile)).addOnCompleteListener(
                            new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    HashMap<String, HashMap<String, String>> userDocs = new HashMap<>();
                                    HashMap<String, String> docObj = new HashMap<>();
                                    docObj.put("Name", fileName + "." + ending);
                                    docObj.put("Status", String.valueOf(WfDoc.DocStatus.Submitted));
                                    docObj.put("UserComments", "");
                                    docObj.put("StaffComments", "");
                                    userDocs.put(fileName, docObj);
                                    FirebaseFirestore.getInstance().collection("Wf-Docs")
                                            .document(loggedInUser.getUid()).set(userDocs);
                                }
                            }
                );

            } catch (IOException ex) {
                Log.d("Document creation error", "Error occurred while creating the file");
            }


        }

    }

    private String getRealPathFromUri(Uri uri) {
//        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, null, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        cursor.moveToFirst();
        String result = cursor.getString(column);
        cursor.close();
        return result;
    }

    private File createDocFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String docFileName = loggedInUser.user.getUid();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File tempFile = File.createTempFile(
                docFileName,  /* prefix */
                ".txt",         /* suffix */
                storageDir      /* directory */
        );

        return tempFile;
    }

//    @Override
//    protected void setUserListener() {
//
//        loggedInUser.setListener(new LoggedInUser.UserDatabaseListener() {
//            @Override
//            public void onImageLoaded(Drawable profileImage) {
//                Log.d("Image-Loaded", String.valueOf(new Date().getTime()));
//                menu.updateProfile(profileImage);
//            }
//
//            @Override
//            public void onUserInfoLoaded(String name, Classification classification, String number) {
//                menu.updateProfile(name);
//                menu.updateMenuItems(classification);
//            }
//
//            @Override
//            public void onClassroomLoaded() {
//                Log.d("ClassroomLoad", "loaded");
//                populatePortal();
//            }
//
//        });
//
//    }

    @Override
    public void updateUi() {

        connectButtons();
        populateDocs();

    }

    public void connectButtons() {
        FloatingActionButton uploadBtn = findViewById(R.id.docUploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mimeTypes =
                        {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain",
                                "application/pdf",
                                "application/zip"};
                startActivityForResult(
                        new Intent(
                                Intent.ACTION_OPEN_DOCUMENT
                        ).setType("*/*").putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes),1
                );
            }
        });
    }

    public void populateDocs() {

        String path = String.format("docs/%s", loggedInUser.getUid());
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(path);

        DocumentAdapter adapter = new DocumentAdapter();
        formsRecycler.setAdapter(adapter);

        ref.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                task.getResult().getItems().forEach(new Consumer<StorageReference>() {
                    @Override
                    public void accept(StorageReference storageReference) {
                        storageReference.getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> uriTask) {
                                        storageReference.getMetadata().addOnCompleteListener(
                                                new OnCompleteListener<StorageMetadata>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<StorageMetadata> metadataTask) {
                                                        storageReference.getDownloadUrl()
                                                                .addOnCompleteListener(
                                                                        new OnCompleteListener<Uri>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Uri> downloadTask) {
//                                                                                wfDocs.add();
                                                                                WfDoc temp = new WfDoc(
                                                                                        loggedInUser,
                                                                                        new WfDoc.DocListener() {
                                                                                            @Override
                                                                                            public void onReady(WfDoc theDoc) {
                                                                                                adapter.addDoc(theDoc);
                                                                                            }
                                                                                        },
                                                                                        storageReference.getName(),
                                                                                        metadataTask.getResult().getCreationTimeMillis(),
                                                                                        downloadTask.getResult().toString()
                                                                                );
                                                                            }
                                                                        }
                                                                );

                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                });
            }
        });
//
//        wfDocs.addAll(course.getAssignments());
//
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//
//                formPager.setAdapter(adapter);
//                formPager.setUserInputEnabled(false);
//
//                TabLayout tabLayout = findViewById(R.id.parentsPortalTabLayout);
//
//                tabLayout.addOnTabSelectedListener(
//                        new TabLayout.OnTabSelectedListener() {
//                            @Override
//                            public void onTabSelected(TabLayout.Tab tab) {
//                                formPager.setCurrentItem(tab.getPosition());
//                            }
//
//                            @Override
//                            public void onTabUnselected(TabLayout.Tab tab) {
//
//                            }
//
//                            @Override
//                            public void onTabReselected(TabLayout.Tab tab) {
//                                formPager.setCurrentItem(tab.getPosition());
//                            }
//
//                        }
//                );
//
//            }
//
//        });

    }

}