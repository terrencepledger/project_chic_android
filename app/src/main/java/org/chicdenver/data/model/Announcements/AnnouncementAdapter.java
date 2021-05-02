package org.chicdenver.data.model.Announcements;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementHolder> {

    ArrayList<Announcement> announcements;
    List<Integer> colors = Arrays.asList(Color.rgb(209, 29, 92),
            Color.rgb(85, 42, 77), Color.rgb(39, 34, 98));

    public AnnouncementAdapter(ArrayList<Announcement> announcements) {

        this.announcements = announcements;

    }

    @NonNull
    @Override
    public AnnouncementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnnouncementHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.announcements_card, parent, false
        ));
    }


    @Override
    public void onBindViewHolder(@NonNull AnnouncementHolder holder, int position) {
        holder.setPost(announcements.get(position));
        holder.setColor(colors.get(position % colors.size()));
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

}

