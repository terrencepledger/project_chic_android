package org.chicdenver.data.model.Documents;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

class DocumentHolder extends RecyclerView.ViewHolder {

    ImageView docDropdownArrow;
    boolean expanded = false;

    TextView docTitle;
    TextView docDate;
    TextView docStatus;
    TextView docStaffComments;
    EditText docUserComments;

    LinearLayout docSecondRowLayout;
    LinearLayout docLastRowLayout;

    public DocumentHolder(View itemView) {
        super(itemView);
        docTitle = itemView.findViewById(R.id.docTitleTextView);
        docDate = itemView.findViewById(R.id.docDateTextView);
        docStatus = itemView.findViewById(R.id.docStatusTextView);
        docStaffComments = itemView.findViewById(R.id.docStaffComments);
        docUserComments = itemView.findViewById(R.id.userCommentsEditText);
        docDropdownArrow = itemView.findViewById(R.id.docArrow);
        docSecondRowLayout = itemView.findViewById(R.id.docSecondRowLayout);
        docLastRowLayout = itemView.findViewById(R.id.docLastRowLayout);
    }

    public void setDocument(String title, String date, String status, String link,
                            String userComments, String staffComments) {
        docTitle.setText(title);
        docDate.setText(date);
        docStatus.setText(status);
        docStaffComments.setText(String.format("%s : %s", staffComments, link));
        docUserComments.setText(userComments);

        docDropdownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!expanded)
                {
                    docSecondRowLayout.setVisibility(View.VISIBLE);
                    docLastRowLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    docSecondRowLayout.setVisibility(View.GONE);
                    docLastRowLayout.setVisibility(View.GONE);
                }
                docDropdownArrow.setRotation(docDropdownArrow.getRotation() + 180);
                expanded = !expanded;
            }
        });
    }

}
