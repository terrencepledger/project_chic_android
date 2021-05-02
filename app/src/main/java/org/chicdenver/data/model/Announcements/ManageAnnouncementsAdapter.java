package org.chicdenver.data.model.Announcements;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

import java.util.ArrayList;
import java.util.Date;

public class ManageAnnouncementsAdapter extends RecyclerView.Adapter<ManageAnnouncementsHolder> {

    private ArrayList<Announcement> announcements;

    public ManageAnnouncementsAdapter() { }

    public void add() {

        Announcement newAnnouncement = new Announcement();
        newAnnouncement.setAnnouncement("New Announcement", "Announcement Text",
                new Date(), null);
        announcements.add(newAnnouncement);
        notifyItemInserted(announcements.size() - 1);

    }

    public void setAnnouncements(ArrayList<Announcement> announcements) {

        this.announcements = announcements;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ManageAnnouncementsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageAnnouncementsHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_announcement, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ManageAnnouncementsHolder holder, int position) {
        holder.bind(announcements.get(position));
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

}
