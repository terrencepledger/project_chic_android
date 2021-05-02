package org.chicdenver.ui.login;

import androidx.annotation.NonNull;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;

import org.chicdenver.data.model.ChicAssignments.ChicCourse;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.ui.main_pages.HomeActivity;
import org.chicdenver.R;
import org.chicdenver.ui.menu.ClassificationMenu;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    ProgressBar loadingProgressBar;

    @Override
    public void onStart() {
        super.onStart();

        GoogleSignInAccount googleUser = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (googleUser != null && firebaseUser != null) {
            ClassificationMenu.welcome(firebaseUser.getDisplayName(), getApplicationContext());
            ClassificationMenu.navigate(this, HomeActivity.class);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(new Scope(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS), new Scope(ClassroomScopes.CLASSROOM_COURSEWORK_ME),
                        new Scope(ClassroomScopes.CLASSROOM_COURSES), new Scope(DriveScopes.DRIVE),
                        new Scope(ClassroomScopes.CLASSROOM_ROSTERS))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if(GoogleSignIn.getLastSignedInAccount(this) != null &&
                FirebaseAuth.getInstance().getCurrentUser() == null) {
            mGoogleSignInClient.revokeAccess();
            mGoogleSignInClient.signOut();
        }

//        loadingProgressBar = findViewById(R.id.loading);

//        TextWatcher afterTextChangedListener = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // ignore
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // ignore
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Log.d("TextWatcher", s.toString());
//            }
//        };
//        usernameEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.addTextChangedListener(afterTextChangedListener);
        SignInButton signInButton = findViewById(R.id.login_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, Integer.parseInt(getString(R.string.GOOGLE_SIGN_IN)));

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Integer.parseInt(getString(R.string.GOOGLE_SIGN_IN))) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult();
                mAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
//                            loadingProgressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    LoggedInUser.BooleanListener listener = new LoggedInUser.BooleanListener() {
                                        @Override
                                        public void onComplete(Boolean bool) {
                                            if(bool)
                                            {
                                                ClassificationMenu.welcome(mAuth.getCurrentUser().getDisplayName(),
                                                        getApplicationContext());
                                                ClassificationMenu.navigate(getActivity(), HomeActivity.class);
                                            }
                                            else
                                            {
                                                signUp(null);
                                            }
                                        }
                                    };
                                    LoggedInUser.isExistingUser(listener,
                                            FirebaseAuth.getInstance().getCurrentUser().getUid());
                                }
                                else
                                    showLoginFailed("Login failed. " + task.getException());
                            }
                        });
            }
            catch (RuntimeException ignored) {

            }
        }
    }

    public void signUp(View v) {
        ClassificationMenu.navigate(getActivity(), SignupActivity.class);
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }

    public Activity getActivity() { return this; }

}