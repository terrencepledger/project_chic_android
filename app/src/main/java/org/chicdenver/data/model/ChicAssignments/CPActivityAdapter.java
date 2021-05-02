package org.chicdenver.data.model.ChicAssignments;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CPActivityAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragments;

    public CPActivityAdapter(Activity activity, List<ChicCourse.ChicAssignment> chicAssignments,
                             String overallGrade)
    {

        super((FragmentActivity) activity);

        fragments = new ArrayList<>(
                Arrays.asList(
                        AssignmentsFragment.newInstance(chicAssignments),
                        GradesFragment.newInstance(chicAssignments, overallGrade)
                )
        );

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

}
