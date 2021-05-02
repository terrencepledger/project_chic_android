package org.chicdenver.ui.parents_pages;

import android.os.Bundle;

import androidx.viewpager2.widget.ViewPager2;

import org.chicdenver.R;
import org.chicdenver.data.model.ChicAssignments.ChicCourse;
import org.chicdenver.ui.DrawerActivity;

import java.util.ArrayList;
import java.util.List;

public class ParentsPortalActivity extends DrawerActivity {

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
    public void updateUi() {

    }


}
