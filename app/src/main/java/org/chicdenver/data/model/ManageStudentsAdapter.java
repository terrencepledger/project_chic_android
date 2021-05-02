package org.chicdenver.data.model;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;
import org.chicdenver.data.model.ChicAssignments.ManageAssignmentAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageStudentsAdapter extends RecyclerView.Adapter<ManageStudentsHolder> {

    private final ArrayList<HashMap> students;

    public Activity getActivity() {
        return activity;
    }

    private final Activity activity;

    public ManageStudentsAdapter(ArrayList<HashMap> students, Activity activity) {

        this.students = students;
        this.activity = activity;

    }

    @NonNull
    @Override
    public ManageStudentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageStudentsHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_student, parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ManageStudentsHolder holder, int position) {
        holder.bind(students.get(position), this);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}
