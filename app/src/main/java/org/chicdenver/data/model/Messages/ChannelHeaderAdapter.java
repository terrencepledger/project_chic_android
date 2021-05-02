package org.chicdenver.data.model.Messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;

import java.util.ArrayList;

public class ChannelHeaderAdapter extends RecyclerView.Adapter<ChannelHeaderHolder> {

    private final ArrayList<String> keys;
    private final String username;
    ArrayList<String> names;
    ArrayList<String> lastMessages;
    ArrayList<String> dates;

    public ChannelHeaderAdapter(Bundle args, ArrayList<String> keys) {

        names = args.getStringArrayList("Names");
        lastMessages = args.getStringArrayList("LastMessages");
        dates = args.getStringArrayList("Dates");
        this.username = args.getString("UserName");
        this.keys = keys;

    }

    @NonNull
    @Override
    public ChannelHeaderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChannelHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.channel_header, parent, false
        ));
    }


    @Override
    public void onBindViewHolder(@NonNull ChannelHeaderHolder holder, int position) {
        holder.setChannelHeader(names.get(position), lastMessages.get(position),
                dates.get(position), username, keys.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

}
