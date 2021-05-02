package org.chicdenver.data.model;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.classroom.model.Course;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.chicdenver.R;
import org.chicdenver.data.model.ChicAssignments.ChicCourse;
import org.chicdenver.data.model.Messages.ConnectionChannel;
import org.chicdenver.ui.login.SignupActivity;
import org.chicdenver.ui.menu.ClassificationMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {
    public final FirebaseUser user;
    private GoogleSignInClient signInClient = null;

    private String uid;
    private FirebaseFirestore db;
    private UserDatabaseListener listener;
    private StorageReference profileImageRef;
    private Activity activity;
    private String displayName;
    private ChicCourse studentClassroom = null;
    public Staff mStaff = null;

    public LoggedInUser(FirebaseUser currentUser, Activity activity, boolean load) {
        user = currentUser;
        uid = user.getUid();
        db = FirebaseFirestore.getInstance();
        this.activity = activity;
        listener = null;

        displayName = user.getDisplayName();

        String path = String.format("images/%s.jpg", user.getUid());
        profileImageRef = FirebaseStorage.getInstance().getReference().child(path);

        if (load)
            startDataLoad();

    }

    public LoggedInUser(String uid, Activity activity) {
        user = null;
        this.uid = uid;
        db = FirebaseFirestore.getInstance();
        this.activity = activity;
        listener = null;

        String path = String.format("images/%s.jpg", uid);
        profileImageRef = FirebaseStorage.getInstance().getReference().child(path);

        loadUserInfo();

    }

    public void setListener(UserDatabaseListener listener) {
        this.listener = listener;
    }

    public String getUserEmail() {
        return user.getEmail();
    }

    public String getDisplayName(int i) {
        if(i == 0)
            return displayName.split(" ")[0];
        return displayName;
    }

    public String getUid() {
        return uid;
    }

    public void startDataLoad() {

        loadProfileImage();

        loadUserInfo();
    }

    public void startStudentLoad() {
        LoggedInUser mUser = this;
        db.collection("Users").document(getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (listener == null)
                            return;
                        DocumentSnapshot dbUser = task.getResult();
                        new Thread(new Runnable() {
                            public void run() {
                                studentClassroom = new ChicCourse(activity,
                                        mUser,
                                        ((DocumentReference) dbUser.get("Group")).getId()
                                );
                                listener.onClassroomLoaded();
                            }
                        }).start();
                    }
                });
    }

    public void startStaffLoad() {
        mStaff = new Staff(db, activity, this, listener);
    }

    public void initUserInfo(String name, String email, String number, String groupId) {

        Map<String, String> dbUser = new HashMap<>();
        dbUser.put("Name", name);
        dbUser.put("Classification", Classification.Unverified.toString());
        dbUser.put("Email", email);
        dbUser.put("Number", number);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();

        user.updateProfile(profileUpdates);

        db.collection("Users").document(user.getUid()).set(dbUser).addOnCompleteListener(

                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            loadUserInfo();
                            addUserToGroup(groupId);
                        } else
                            Log.e("Init error", task.getException().toString());

                    }
                }

        );

    }

    public void initUserInfo(String name, String email, String number, String groupId, File image) {

        Map<String, String> dbUser = new HashMap<>();
        dbUser.put("Name", name);
        dbUser.put("Classification", Classification.Unverified.toString());
        dbUser.put("Number", number);
        dbUser.put("Email", email);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();

        user.updateProfile(profileUpdates);

        db.collection("Users").document(user.getUid()).set(dbUser).addOnCompleteListener(

                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            loadUserInfo();
                            addUserToGroup(groupId);
                        } else
                            Log.e("Init error", task.getException().toString());

                    }
                }
        );

        updateProfileImage(image);

    }

    public void addUserToGroup(String id) {

        DocumentReference groupRef = db.document("/Groups/" + id + "/");
        db.collection("Users").document(getUid()).update("Group",
                groupRef);
//        groupRef.get().addOnCompleteListener(
//
//                new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                        HashMap<String, String> user = new HashMap();
//                        user.put("Name", name);
//                        user.put("ID", getUid());
//
//
//
//                    }
//                }
//        );
    }

    private void loadUserInfo() throws NullPointerException {

        db.collection("Users").document(getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (listener == null)
                            return;

                        DocumentSnapshot dbUser = task.getResult();

                        try {
                            String name = dbUser.get("Name").toString();
                            Log.d("UserName", name);
                            Classification classification = Classification.valueOf(
                                    dbUser.get("Classification").toString());
                            String number = dbUser.get("Number").toString();

                            displayName = name;
                            listener.onUserInfoLoaded(name, classification, number);
                        }
                        catch (NullPointerException e) {
                            ClassificationMenu.navigate(activity, SignupActivity.class);
                        }
                    }
                });

    }

    private void loadProfileImage() {

        File imageFile = null;
        try {
            imageFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            Log.e("File-Creation-Error", e.getMessage());
        }

        Uri fileUri = Uri.fromFile(imageFile);

        profileImageRef.getFile(fileUri).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if (listener == null)
                    return;
                Drawable temp;
                if (task.isSuccessful())
                    temp = Drawable.createFromPath(fileUri.getPath());
                else {
                    temp = activity.getDrawable(R.drawable.placeholder_avatar);
                    Log.e("Image download error", String.valueOf(task.getException()));
                }
                listener.onImageLoaded(temp);

            }
        });

    }

    public void updateProfileImage(File image) {
        // TODO implement user photo uri method instead
        try {
            profileImageRef.putStream(new FileInputStream(image))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            try {
                                listener.onImageLoaded(Drawable.createFromStream(activity.getContentResolver()
                                        .openInputStream(Uri.fromFile(image)), image.toString()));
                            } catch (FileNotFoundException e) {
                                loadProfileImage();
                                Log.e("Listener-Load-Error", e.toString());
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("Image upload error", exception.getMessage());
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void updateName(String name) {

        Map<String, String> dbUser = new HashMap<>();
        dbUser.put("Name", name);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();

        user.updateProfile(profileUpdates);

        db.collection("Users").document(user.getUid())
                .update("Name", name);

        db.collection("Users").document(user.getUid())
                .get().addOnCompleteListener(

                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentReference groupRef =
                                task.getResult().get("Group", DocumentReference.class);
                        if(groupRef != null)
                            groupRef.get().addOnCompleteListener(
                                    new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            List<Map<String, String>> students = (List<Map<String, String>>)
                                                    task.getResult().get("Students");
                                            for (Map<String, String> student : students) {
                                                if (student.get("ID").equals(getUid()))
                                                    student.replace("Name", name);
                                            }
                                            groupRef.update("Students", students);
                                        }
                                    }
                            );
                    }
                }

        );

        loadUserInfo();

    }

    public void updateNumber(String number) {

        db.collection("Users").document(user.getUid())
                .update("Name", number);
        loadUserInfo();

    }

    public void updateClassification(Classification classification) {

        db.collection("Users").document(user.getUid())
                .update("Classification", classification.toString());
        loadUserInfo();
    }

    public ChicCourse getStudentClassroom() {
        return studentClassroom;
    }

    public interface UserDatabaseListener {

        void onImageLoaded(Drawable profileImage);

        void onUserInfoLoaded(String name, Classification classification, String number);

        void onClassroomLoaded();

    }

    public static void isExistingUser(BooleanListener listener, String uid) {

        FirebaseFirestore.getInstance().collection("Users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        listener.onComplete(task.getResult().exists());
                    }
                });

    }

    public interface BooleanListener {
        void onComplete(Boolean bool);
    }

    public static class Staff {

        private final Activity activity;
        private final LoggedInUser user;
        private final FirebaseFirestore db;
        private ArrayList<ChicCourse> allClassrooms = new ArrayList<>();

        public Staff(FirebaseFirestore db, Activity activity, LoggedInUser user,
                     UserDatabaseListener listener) {

            this.activity = activity;
            this.user = user;
            this.db = db;

            db.collection("Groups").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            List<DocumentSnapshot> docs = task.getResult().getDocuments();

                            new Thread(new Runnable() {
                                public void run() {
                                    for (DocumentSnapshot doc : docs) {
                                        if (doc.getId().equals("Ungrouped"))
                                            continue;
                                        ChicCourse temp = new ChicCourse(activity, user, doc.getId());
                                        temp.setCourseName(doc.get("Name").toString());
                                        try {
                                            if(temp.isActive())
                                                allClassrooms.add(temp);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    listener.onClassroomLoaded();
                                }
                            }).start();

                        }

                    });

        }

        public void addCourse(String name) {

            Course course = new Course();
            course.setName(name);

            course = ChicCourse.createCourse(activity, course);

            HashMap<String, String> hashName = new HashMap<>();
            hashName.put("Name", name);

            Log.d("Course-ID", course.getId());

            db.collection("Groups").document(course.getId()).set(hashName).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(activity, "Please go to google classroom online " +
                                    "and accept the course invitation", Toast.LENGTH_LONG).show();
                        }
                    }
            );

            ConnectionChannel courseChannel = new ConnectionChannel();
            courseChannel.setChannelId(course.getId());
            courseChannel.addMember(user);

            FirebaseDatabase.getInstance().getReference().child("Channels")
                    .push().setValue(courseChannel);

        }

        public ChicCourse.ChicAssignment getNextAssignment() {

            if(allClassrooms.size() != 0)
                return allClassrooms.get(0).getNextAssignment();

            return null;

        }

        public ArrayList<ChicCourse> getAllClassrooms() {
            return allClassrooms;
        }

    }
}