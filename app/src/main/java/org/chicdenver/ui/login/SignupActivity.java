package org.chicdenver.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.chicdenver.R;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.ui.main_pages.HomeActivity;
import org.chicdenver.ui.menu.ClassificationMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SignupActivity extends AppCompatActivity {

    private boolean imageChanged = false;
    private File imageFile;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Spinner spinner;
    private HashMap<String, String> schools = new HashMap<>();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_signup);

        populateSpinner();

    }

    @Override
    public boolean onSupportNavigateUp() {
        mAuth.signOut();
        ClassificationMenu.navigate(this, LoginActivity.class);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Integer.parseInt(getString(R.string.ACTION_GET_FROM_GALLERY)) && resultCode == Activity.RESULT_OK) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                // Copying
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();
                imageFile = photoFile;

                setProfileImage(Uri.fromFile(photoFile));
                Log.d("File exists: ", String.valueOf(photoFile.exists()));

            } catch (IOException ex) {
                Log.d("Image creation error", "Error occurred while creating the file");
            }


        }

        else if (requestCode == Integer.parseInt(getString(R.string.GOOGLE_SIGN_IN))) {


            mAuth.signInWithCredential(GoogleAuthProvider.getCredential(
                    GoogleSignIn.getLastSignedInAccount(this).getIdToken(), null))
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestEmail()
                                        .requestIdToken(getString(R.string.default_web_client_id))
                                        .requestScopes(new Scope(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS),
                                                new Scope(ClassroomScopes.CLASSROOM_COURSEWORK_ME),
                                                new Scope(ClassroomScopes.CLASSROOM_COURSES),
                                                new Scope(DriveScopes.DRIVE),
                                                new Scope(ClassroomScopes.CLASSROOM_ROSTERS))
                                        .build();
                                GoogleSignInClient mGoogleSignInClient =
                                        GoogleSignIn.getClient(getActivity(), gso);
                                updateUser(user);
                            } else
                                Log.e("Login-Failed", task.getException().toString());
                        }
                    });

        }

    }

    public void sendNotificationEmail(LoggedInUser user) {
        final String username = "chicstaff2020@gmail.com";
        final String password = getString(R.string.GMAIL_PASSWORD);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(username));
            message.setSubject("New Student Signup");
            message.setText(String.format("Dear CHIC Staff, %s has signed up! Head to the console (https://chic-staff-console.000webhostapp.com/#) to approve them.", user.getDisplayName(1)));

//            MimeBodyPart messageBodyPart = new MimeBodyPart();
//
//            Multipart multipart = new MimeMultipart();
//
//            messageBodyPart = new MimeBodyPart();
//            String file = "path of file to be attached";
//            String fileName = "attachmentName";
//            DataSource source = new FileDataSource(file);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName(fileName);
//            multipart.addBodyPart(messageBodyPart);
//
//            message.setContent(multipart);

            Transport.send(message);

            Log.d("Send-Notification-Email", "Done");

        } catch (MessagingException e) {
            Log.d("Send-Notification-Email", "Failed: " + e.toString());
            throw new RuntimeException(e);
        }
    }

    public void populateSpinner() {

        spinner = findViewById(R.id.classificationSpinner);

        ArrayList<String> spinnerArray = new ArrayList<>();

        Activity activity = this;

        FirebaseFirestore.getInstance().collection("Groups").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<DocumentSnapshot> docs = task.getResult().getDocuments();

                        for (DocumentSnapshot doc : docs) {
                            if(!doc.getId().equals("Ungrouped")) {
                                schools.put(doc.get("Name").toString(), doc.getId());
                                spinnerArray.add(doc.get("Name").toString());
                            }
                        }

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                (activity, android.R.layout.simple_spinner_item,
                                        spinnerArray); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerArrayAdapter);
                        spinner.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                                               int i, long l) {
                                        Log.d("Selection", schools.get(adapterView
                                                .getItemAtPosition(i).toString()
                                        ));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }
                                }
                        );

                    }

                });

    }

    public boolean inputValid() {

        String name = ((EditText) findViewById(R.id.signupName)).getText().toString();
        String number = ((EditText) findViewById(R.id.signupNumber)).getText().toString();

        return (name.length() > 0 && number.length() > 0);

    }

    public void signUp(View view) {

        if(inputValid())
        {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestScopes(new Scope(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS), new Scope(ClassroomScopes.CLASSROOM_COURSEWORK_ME),
                            new Scope(ClassroomScopes.CLASSROOM_COURSES), new Scope(DriveScopes.DRIVE))
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, Integer.parseInt(getString(R.string.GOOGLE_SIGN_IN)));
        }
        else
            Toast.makeText(this, "Please enter name and number", Toast.LENGTH_SHORT)
                    .show();

    }

    private Activity getActivity() {
        return this;
    }

    Classification getClassification() {
        return Classification.valueOf(
                String.valueOf(
                        ((RadioButton)findViewById(
                            ((RadioGroup)findViewById(R.id.classificationRadioGroup)).getCheckedRadioButtonId()
                        )).getText()
                )
        );
    }

    private void updateUser(FirebaseUser user) {

        String name = ((EditText) findViewById(R.id.signupName)).getText().toString();
        String number = ((EditText) findViewById(R.id.signupNumber))
                .getText().toString();

        LoggedInUser loggedInUser = new LoggedInUser(user, this, false);
        String groupId;
        if(((RadioGroup)findViewById(R.id.classificationRadioGroup)).getCheckedRadioButtonId()
            == findViewById(R.id.studentRadioBtn).getId())
        {
            groupId = schools.get(spinner.getSelectedItem().toString());
        }
        else
            groupId = getString(R.string.wf_id);

        if (imageChanged)
            loggedInUser.initUserInfo(name, user.getEmail(), number, groupId, imageFile);
        else {
            loggedInUser.initUserInfo(name, user.getEmail(), number, groupId);
            ClassificationMenu.welcome(mAuth.getCurrentUser().getDisplayName(),
                    getApplicationContext());
            ClassificationMenu.navigate(getActivity(), HomeActivity.class);
        }

        loggedInUser.setListener(new LoggedInUser.UserDatabaseListener() {
            @Override
            public void onImageLoaded(Drawable profileImage) {

                ClassificationMenu.welcome(mAuth.getCurrentUser().getDisplayName(),
                        getApplicationContext());
                ClassificationMenu.navigate(getActivity(), HomeActivity.class);

            }

            @Override
            public void onUserInfoLoaded(String name, Classification classification, String number) {
            }

            @Override
            public void onClassroomLoaded() {

            }

        });

        new Thread(new Runnable() {
            public void run() {
                sendNotificationEmail(loggedInUser);
            }
        }).start();

    }

    public void profileImageOnClick(View view) {

        startActivityForResult(
                new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
                ),
                Integer.parseInt(getString(R.string.ACTION_GET_FROM_GALLERY))
        );

    }

    public void radioBtnsOnClick(View btn) {

        if(btn.getId() == R.id.studentRadioBtn)
        {
            findViewById(R.id.selectSchoolTextView).setVisibility(View.VISIBLE);
            findViewById(R.id.selectSchoolCardView).setVisibility(View.VISIBLE);
        }
        else
        {
            findViewById(R.id.selectSchoolTextView).setVisibility(View.GONE);
            findViewById(R.id.selectSchoolCardView).setVisibility(View.GONE);
        }

        Log.d("Class", getClassification().toString());

    }

    private void setProfileImage(Uri image) {

        ImageView imageView = findViewById(R.id.signupProfileImage);
        imageView.setImageDrawable(Drawable.createFromPath(image.getPath()));
        imageView.setTag(image.getPath());

        imageChanged = true;

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

}