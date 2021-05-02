package org.chicdenver.data.model.ChicAssignments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

import java.util.ArrayList;
import java.util.List;

public class AssignmentsAdapter extends RecyclerView.Adapter<AssignmentHolder> {

    private final List<ChicCourse.ChicAssignment> assignments;
    ArrayList<String> titles;
    ArrayList<String> descs;
    ArrayList<String> grades;
    ArrayList<String> remTimes;
    ArrayList<String> dueDates;

    public AssignmentsAdapter(Bundle args, List<ChicCourse.ChicAssignment> assignments) {

        titles = args.getStringArrayList("TitleTexts");
        descs = args.getStringArrayList("DescTexts");
        grades = args.getStringArrayList("GradeTexts");
        remTimes = args.getStringArrayList("RemTimeTexts");
        dueDates = args.getStringArrayList("DueDateTexts");

        this.assignments = assignments;

    }

    @NonNull
    @Override
    public AssignmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssignmentHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.assignment, parent, false
        ));
    }


    @Override
    public void onBindViewHolder(@NonNull AssignmentHolder holder, int position) {
        holder.setAssignment(titles.get(position), descs.get(position),
                grades.get(position), remTimes.get(position), dueDates.get(position),
                assignments.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

}
