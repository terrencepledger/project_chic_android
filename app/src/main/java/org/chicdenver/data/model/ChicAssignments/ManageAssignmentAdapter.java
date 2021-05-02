package org.chicdenver.data.model.ChicAssignments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.classroom.model.Date;

import org.chicdenver.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ManageAssignmentAdapter extends RecyclerView.Adapter<ManageAssignmentAdapter.RecyclerViewHolder> {

    private final ChicCourse course;
    List<ChicCourse.ChicAssignment> data;


    public ManageAssignmentAdapter(List<ChicCourse.ChicAssignment> assignments, ChicCourse course) {

        data = assignments;
        this.course = course;

    }

    public void add(ChicCourse.ChicAssignment assignment) {

        data.add(assignment);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.manage_assignment, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.header.setText(data.get(position).getTitle());
        holder.assignmentTitle.setText(data.get(position).getTitle());
        holder.dueDate.setText(data.get(position).getPrettyDate());
        holder.desc.setText(data.get(position).getDescription());
        holder.points.setText(String.valueOf(data.get(position).getPoints()));
        if(!data.get(position).isGraded())
            holder.submitBtn.setVisibility(View.VISIBLE);
        holder.submitBtn.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    course.update(data.get(position).setCourseWork(

                                            holder.assignmentTitle.getText().toString(),
                                            getDueDate(holder.dueDate.getText().toString()),
                                            holder.desc.getText().toString(),
                                            Double.parseDouble(holder.points.getText().toString())

                                    ));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }

        );
    }

    public Date getDueDate(String inputDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy", Locale.US);

        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(formatter.parse(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date date = new Date();
        date.setYear(cal.get(Calendar.YEAR));
        date.setMonth(cal.get(Calendar.MONTH) + 1);
        date.setDay(cal.get(Calendar.DAY_OF_MONTH));

        return date;

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView header;
        TextView assignmentTitle;
        TextView dueDate;
        TextView desc;
        TextView points;
        Button submitBtn;

        public RecyclerViewHolder(@NonNull View itemView) {

            super(itemView);
            header = itemView.findViewById(R.id.mngAssignmentHeader);
            assignmentTitle = itemView.findViewById(R.id.assignmentTitleTextInput);
            dueDate = itemView.findViewById(R.id.assignmentDueDateInput);
            desc = itemView.findViewById(R.id.assignmentDescTextInput);
            points = itemView.findViewById(R.id.assignmentMaxPointsInput);
            submitBtn = itemView.findViewById(R.id.manageAssignmentSubmitBtn);

            header.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ConstraintLayout layout = itemView.findViewById(R.id.assignmentInfoConstraint);
                            if(layout.getVisibility() != View.GONE) {
                                layout.setVisibility(View.GONE);
                            }
                            else
                            {
                                layout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
            );

        }

    }

}
