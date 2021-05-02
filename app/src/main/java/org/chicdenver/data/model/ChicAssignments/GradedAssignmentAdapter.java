package org.chicdenver.data.model.ChicAssignments;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

import java.util.ArrayList;

public class GradedAssignmentAdapter extends RecyclerView.Adapter<GradedAssignmentHolder> {

    ArrayList<String> titles;
    ArrayList<String> grades;

    public GradedAssignmentAdapter(ArrayList<String> titles, ArrayList<String> grades) {

        this.titles = titles;
        this.grades = grades;

    }

    @NonNull
    @Override
    public GradedAssignmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GradedAssignmentHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.graded_assignment, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull GradedAssignmentHolder holder, int position) {
        holder.bind(titles.get(position), grades.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}
