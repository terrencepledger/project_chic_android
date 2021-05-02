package org.chicdenver.ui.student_pages;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import org.chicdenver.R;
import org.chicdenver.data.model.ChicAssignments.CPActivityAdapter;
import org.chicdenver.data.model.ChicAssignments.ChicCourse;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.ui.DrawerActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassPortalActivity extends DrawerActivity {

    List<ChicCourse.ChicAssignment> chicAssignments = new ArrayList<>();
    ViewPager2 cpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_portal);

        activity = this;

        cpPager = findViewById(R.id.parentsPortalPager);

        init();

        loggedInUser.startStudentLoad();

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
                Log.d("ClassroomLoad", "loaded");
                populatePortal();
            }

        });

    }

    @Override
    public void updateUi() {


    }

    public void populatePortal() {

        ChicCourse course = loggedInUser.getStudentClassroom();

        chicAssignments.addAll(course.getAssignments());

        CPActivityAdapter adapter = new CPActivityAdapter(activity, chicAssignments,
                course.getOverallGrade()
        );

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                cpPager.setAdapter(adapter);
                cpPager.setUserInputEnabled(false);

                TabLayout tabLayout = findViewById(R.id.parentsPortalTabLayout);

                tabLayout.addOnTabSelectedListener(
                        new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                cpPager.setCurrentItem(tab.getPosition());
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {

                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {
                                cpPager.setCurrentItem(tab.getPosition());
                            }

                        }
                );

            }

        });

    }

}