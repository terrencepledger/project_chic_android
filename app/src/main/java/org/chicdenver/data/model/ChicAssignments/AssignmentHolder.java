package org.chicdenver.data.model.ChicAssignments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

public class AssignmentHolder extends RecyclerView.ViewHolder {

    Button submitBtn;
    Button docBtn;
    TextView titleText;
    TextView descText;
    TextView gradeText;
    TextView remainingTimeText;
    TextView dueDateText;

    public AssignmentHolder(View itemView) {
        super(itemView);
        titleText = itemView.findViewById(R.id.assignmentTitleText);
        descText = itemView.findViewById(R.id.assignmentDescText);
        gradeText = itemView.findViewById(R.id.assignmentGradeText);
        dueDateText = itemView.findViewById(R.id.assignemntDueDateText);
        remainingTimeText = itemView.findViewById(R.id.assignmentRemainingTimeText);

        submitBtn = itemView.findViewById(R.id.assignmentSubBtn);

        docBtn = itemView.findViewById(R.id.assignmentDocBtn);

    }

    public void setAssignment(String title, String desc, String grade, String remTime,
                              String dueDate, ChicCourse.ChicAssignment assignment) {

        titleText.setText(title);
        descText.setText(desc);
        gradeText.setText(grade);
        if(remTime != null && (Integer.parseInt(remTime) < 8 & Integer.parseInt(remTime) > -1))
        {
            remainingTimeText.setText(String.format("%s days left", remTime));
            remainingTimeText.setVisibility(View.VISIBLE);
        }
        dueDateText.setText(dueDate);

        check(assignment);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assignment.submit();
            }
        });

        docBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(assignment.getDocumentLink()));
                        itemView.getContext().startActivity(intent);
                    }
                }).start();
            }
        });

    }

    public void check(ChicCourse.ChicAssignment assignment) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (assignment.isSubmitted()) {
                    ((Activity) itemView.getContext()).runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    gradeText.setText("SUBMITTED");
                                    submitBtn.setVisibility(View.GONE);
                                    itemView.findViewById(R.id.assignmentBtnsSpacer).setVisibility(View.GONE);
                                }
                            }
                    );
                }
                if (assignment.isGraded()) {
                    ((Activity) itemView.getContext()).runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    submitBtn.setVisibility(View.GONE);
                                    itemView.findViewById(R.id.assignmentBtnsSpacer).setVisibility(View.GONE);
                                }
                            }
                    );
                }
            }
        }).start();

    }

}
