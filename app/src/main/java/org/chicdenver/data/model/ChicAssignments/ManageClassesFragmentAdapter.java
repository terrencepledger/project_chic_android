package org.chicdenver.data.model.ChicAssignments;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.data.model.Messages.ConnectionChannel;

import java.util.ArrayList;
import java.util.Arrays;

public class ManageClassesFragmentAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> frags;

    public ManageClassesFragmentAdapter(Activity activity, LoggedInUser user,
                                        ArrayList<ChicCourse> courses){
        super((FragmentActivity) activity);
        frags = new ArrayList<>(Arrays.asList(
                ClassAssignmentsListFragment.newInstance(courses, user)));

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return frags.get(position);
    }

    @Override
    public int getItemCount() {
        return frags.size();
    }



}
