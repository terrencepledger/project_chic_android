package org.chicdenver.data.model.ChicAssignments;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

public class GradedAssignmentHolder extends RecyclerView.ViewHolder {

    TextView gradedAssignmentTitleText;
    TextView gradedAssignmentGradeText;

    public GradedAssignmentHolder(@NonNull View itemView) {
        super(itemView);

        gradedAssignmentTitleText = itemView.findViewById(R.id.gradedAssignmentTitleText);
        gradedAssignmentGradeText = itemView.findViewById(R.id.gradedAssignmentGradeText);

    }

    public void bind(String title, String grade) {

        gradedAssignmentTitleText.setText(title);
        gradedAssignmentGradeText.setText(grade);

    }

}
