package org.chicdenver.ui.staff_pages;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.chicdenver.R;
import org.chicdenver.data.model.ChicAssignments.ManageClassesFragmentAdapter;
import org.chicdenver.data.model.ChicAssignments.ChicCourse;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.ui.DrawerActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManageClassesActivity extends DrawerActivity {

    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_classes);

        activity = this;

        init();

        loggedInUser.startStaffLoad();

    }

    @Override
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
                List<ChicCourse> courseList = loggedInUser.mStaff.getAllClassrooms();
                populateClasses(courseList);
            }

        });

    }

    public void populateClasses(List<ChicCourse> courses) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPager2.setAdapter(
                        new ManageClassesFragmentAdapter(activity,
                                loggedInUser, (ArrayList<ChicCourse>) courses
                        )
                );
            }
        });

    }

    @Override
    public void updateUi() {

        viewPager2 = findViewById(R.id.manageClassesViewPager);

    }

}
