package org.chicdenver.data.model.ChicAssignments;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

public class UpcomingAssignmentHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView dateText;
        TextView descriptionText;

        public UpcomingAssignmentHolder(@NonNull View itemView) {
            super(itemView);
            this.titleText = itemView.findViewById(R.id.upcomingTitleText);
            this.dateText = itemView.findViewById(R.id.upcomingDateText);
            this.descriptionText = itemView.findViewById(R.id.upcomingDescriptionText);
        }

        public void setAssignment(ChicCourse.ChicAssignment assignment) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    titleText.setText(assignment.getTitle());
                    dateText.setText(String.format("Due Date:\n %s", assignment.getPrettyDate()));
                    descriptionText.setText(assignment.getDescription());

                }

            });
        }

}