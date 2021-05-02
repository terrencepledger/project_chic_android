package org.chicdenver.data.model.Announcements;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;


public class AnnouncementHolder extends RecyclerView.ViewHolder {

    TextView titleText;
    TextView dateText;
    TextView descText;

    public AnnouncementHolder(@NonNull View itemView) {

        super(itemView);

        this.titleText = itemView.findViewById(R.id.postTitleText);
        this.dateText = itemView.findViewById(R.id.postDateText);
        this.descText = itemView.findViewById(R.id.ancsDescriptionText);

        itemView.findViewById(R.id.ancsTitleCard).setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        itemView.findViewById(R.id.ancsDescCard).setVisibility(View.VISIBLE);
                        return false;
                    }
                }
        );

    }

    public void setPost(Announcement announcement) {

        titleText.setText(announcement.getTitle());
        dateText.setText(announcement.getPrettyDate());
        descText.setText(announcement.getAnnouncement());

    }

    public void setColor(Integer color) {
        ((CardView) itemView.findViewById(R.id.ancsTitleCard)).setCardBackgroundColor(color);
    }
}
