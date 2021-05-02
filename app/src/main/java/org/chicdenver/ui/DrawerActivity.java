package org.chicdenver.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.chicdenver.R;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.ui.login.LoginActivity;
import org.chicdenver.ui.login.SignupActivity;
import org.chicdenver.ui.menu.ClassificationMenu;
import org.chicdenver.ui.menu.MenuAdapter;

import java.util.Date;

public abstract class DrawerActivity extends AppCompatActivity {

    protected FirebaseAuth auth;
    protected LoggedInUser loggedInUser;
    protected Toolbar toolbar;
    protected MenuAdapter menu;
    protected Activity activity;

    public void init() {

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        auth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = auth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(new Scope(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS),
                        new Scope(ClassroomScopes.CLASSROOM_COURSEWORK_ME),
                        new Scope(ClassroomScopes.CLASSROOM_COURSES), new Scope(DriveScopes.DRIVE),
                        new Scope(ClassroomScopes.CLASSROOM_ROSTERS))
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if(firebaseUser != null) {
            loggedInUser = new LoggedInUser(firebaseUser, activity, true);
        }
        else
            ClassificationMenu.navigate(activity, LoginActivity.class);

        toolbar = activity.findViewById(R.id.toolbar);

        menu = new MenuAdapter(activity, loggedInUser, toolbar);
        menu.setIndicator(getIntent().getLongExtra("ITEM_POS", 0));

        setUserListener();

        updateUi();

    }

    protected void setUserListener() {

        loggedInUser.setListener(new LoggedInUser.UserDatabaseListener() {

            @Override
            public void onImageLoaded(Drawable profileImage) {
                Log.d("Image-Loaded", String.valueOf(new Date().getTime()));
                menu.updateProfile(profileImage);
            }

            @Override
            public void onUserInfoLoaded(String name, Classification classification, String number) {
                menu.updateProfile(name);
                menu.updateMenuItems(classification);
            }

            @Override
            public void onClassroomLoaded() {

            }

        });

    }

    public abstract void updateUi();

}
