package org.chicdenver.data.model.Announcements;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.chicdenver.R;
import org.chicdenver.data.model.Classification;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ManageAnnouncementsHolder extends RecyclerView.ViewHolder {

    TextView titleText;
    TextView dateText;
    TextView descText;
    Button submitBtn;


    public ManageAnnouncementsHolder(@NonNull View itemView) {

        super(itemView);

        titleText = itemView.findViewById(R.id.manageAncsTitleText);
        dateText = itemView.findViewById(R.id.manageAncsDateText);
        descText = itemView.findViewById(R.id.ancsDescriptionText);
        submitBtn = itemView.findViewById(R.id.manageAncSubmitBtn);

        titleText.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        convertTexts(titleText, itemView.findViewById(R.id.manageAncsTitleEditText));
                        return false;
                    }
                }
        );

        dateText.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        convertTexts(dateText, itemView.findViewById(R.id.manageAncsDateEditText));
                        return false;
                    }
                }
        );
        descText.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        convertTexts(descText, itemView.findViewById(R.id.manageAncsDescEditText));
                        return false;
                    }
                }
        );

        bindSubmit();

    }

    public void bind(Announcement announcement) {

        titleText.setText(announcement.getTitle());
        dateText.setText(announcement.getPrettyDate());
        descText.setText(announcement.getAnnouncement());

    }

    public void bindSubmit() {

        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference().child("Announcements").getRef();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                convertTexts(titleText, itemView.findViewById(R.id.manageAncsTitleEditText));
                convertTexts(dateText, itemView.findViewById(R.id.manageAncsDateEditText));
                convertTexts(descText, itemView.findViewById(R.id.manageAncsDescEditText));

                ArrayList<Classification> audienceList = new ArrayList<>();
                CheckBox studentBox = itemView.findViewById(R.id.studentCheckBox);
                CheckBox parentBox = itemView.findViewById(R.id.parentCheckBox);
                CheckBox wfBox = itemView.findViewById(R.id.wfCheckBox);
                if(studentBox.isChecked())
                    audienceList.add(Classification.Student);
                if(parentBox.isChecked())
                    audienceList.add(Classification.Parent);
                if(wfBox.isChecked())
                    audienceList.add(Classification.Workforce);

                Announcement newAnnouncement = new Announcement();
                String newDate = dateText.getText().toString();
                newDate += " 2020";
                Date parsedDate = new SimpleDateFormat("MMM dd yyyy", Locale.US)
                        .parse(newDate, new ParsePosition(0));
                newAnnouncement.setAnnouncement(
                     titleText.getText().toString(), descText.getText().toString(),
                     parsedDate,
                        audienceList
                );

                dbRef.push().setValue(newAnnouncement);

            }
        });

    }

    public void convertTexts(TextView textView, EditText et) {

        LinearLayout classificationBoxes = itemView.findViewById(R.id.classifcationBoxesLayout);
        CardView descCard = itemView.findViewById(R.id.ancsDescCard);

        if(textView.getVisibility() == View.VISIBLE)
        {
            et.setText(textView.getText());
            textView.setVisibility(View.INVISIBLE);
            et.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.VISIBLE);
            classificationBoxes.setVisibility(View.VISIBLE);
            descCard.setVisibility(View.VISIBLE);
        }
        else
        {
            textView.setText(et.getText());
            et.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.GONE);
            classificationBoxes.setVisibility(View.GONE);
            descCard.setVisibility(View.GONE);
        }

    }

}
