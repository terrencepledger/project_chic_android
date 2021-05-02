package org.chicdenver.data.model.ChicAssignments;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.classroom.model.Course;

import org.chicdenver.R;
import org.chicdenver.data.model.LoggedInUser;

public class ManageClassAssignmentsListHolder extends RecyclerView.ViewHolder {

    private final LoggedInUser user;
    TextView courseName;
    RecyclerView courseAssignmentListRecycler;

    public ManageClassAssignmentsListHolder(@NonNull View itemView, LoggedInUser user) {
        super(itemView);

        this.user = user;

        courseName = itemView.findViewById(R.id.assignmentsListCourseNameText);
        courseAssignmentListRecycler = itemView.findViewById(R.id.assignmentsListRecycler);

        itemView.findViewById(R.id.assignmentsListDropdown).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(courseAssignmentListRecycler.getVisibility() == View.VISIBLE) {
                            courseAssignmentListRecycler.setVisibility(View.GONE);
                            itemView.findViewById(R.id.assignmentsListDropdown).setBackground(
                                    itemView.getContext().getDrawable(R.drawable.path_18)
                            );
                        }
                        else {
                            courseAssignmentListRecycler.setVisibility(View.VISIBLE);
                            itemView.findViewById(R.id.assignmentsListDropdown).setBackground(
                                    itemView.getContext().getDrawable(R.drawable.path_19)
                            );
                        }
                    }
                }
        );

    }

    public void setCourse(ChicCourse course) {

        courseName.setText(course.getCourseName());

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        ManageAssignmentAdapter assignmentAdapter =
                                new ManageAssignmentAdapter(course.getAssignments(), course);
                        ((Activity) itemView.getContext()).runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        courseAssignmentListRecycler.setAdapter(
                                                assignmentAdapter
                                        );
                                        itemView.findViewById(R.id.assignmentsListAddBtn).setOnClickListener(
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        assignmentAdapter.add(
                                                                course.createAssignment(user)
                                                        );
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        ).start();
    }

}
