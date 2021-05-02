package org.chicdenver.data.model.ChicAssignments;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;
import org.chicdenver.data.model.LoggedInUser;

import java.util.List;

public class ManageClassAssignmentsListAdapter extends RecyclerView.Adapter<ManageClassAssignmentsListHolder> {

    private final List<ChicCourse> courses;
    private final LoggedInUser user;

    public ManageClassAssignmentsListAdapter(List<ChicCourse> courses, LoggedInUser user) {
        this.courses = courses;
        this.user = user;
    }

    @NonNull
    @Override
    public ManageClassAssignmentsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageClassAssignmentsListHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.manage_class_assignments_list, parent, false
        ), user);
    }


    @Override
    public void onBindViewHolder(@NonNull ManageClassAssignmentsListHolder holder, int position) {
        holder.setCourse(courses.get(position));
    }

    @Override
    public int getItemCount() {
        try {
            return courses.size();
        }
        catch (NullPointerException e)
        {
            return 0;
        }
    }

}
