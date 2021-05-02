package org.chicdenver.data.model.Messages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.chicdenver.R;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MessageAdapter extends RecyclerView.Adapter<ChannelMessageHolder> {

    ArrayList<String> messageTexts;
    ArrayList<String> creatorTexts;
    ArrayList<String> dateTexts;

    public MessageAdapter(Bundle args) {
        this.messageTexts = args.getStringArrayList("MessageTexts");
        this.creatorTexts = args.getStringArrayList("CreatorTexts");
        this.dateTexts = args.getStringArrayList("DateTexts");
    }

    public void add(String messageText, String creatorText, String dateText) {
        Log.d("Added", ":)");
        messageTexts.add(messageText);
        creatorTexts.add(creatorText);
        dateTexts.add(dateText);
        notifyDataSetChanged();

    }

    @Override
    public ChannelMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChannelMessageHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.channel_message, parent, false));
    }

    @Override
    public void onBindViewHolder(ChannelMessageHolder holder, int position) {

        holder.bind(messageTexts.get(position), creatorTexts.get(position),
                dateTexts.get(position));

    }

    @Override
    public int getItemCount() {
        return messageTexts.size();
    }


    public void update(ConnectionChannel value) {
        this.messageTexts.clear();
        this.creatorTexts.clear();
        this.dateTexts.clear();
        MessageAdapter mThis = this;
        value.getMessages().forEach(
                new Consumer<ConnectionChannel.ChannelMessage>() {
                    @Override
                    public void accept(ConnectionChannel.ChannelMessage channelMessage) {
                        mThis.messageTexts.add(channelMessage.getMessage());
                        mThis.creatorTexts.add(channelMessage.getCreator());
                        mThis.dateTexts.add(channelMessage.getPrettyPostTime());
                    }
                }
        );
        notifyDataSetChanged();
    }
}

class ChannelMessageHolder extends RecyclerView.ViewHolder {

    private View itemView;

    private TextView messageText;
    private TextView creatorText;
    private TextView postTimeText;

    public ChannelMessageHolder(View itemView) {

        super(itemView);

        this.itemView = itemView;

    }

    public void bind(String message, String creator, String date) {

        TextView messageText = itemView.findViewById(R.id.messageText);
        TextView creatorText = itemView.findViewById(R.id.creatorText);
        TextView postTimeText = itemView.findViewById(R.id.postTimeText);

        messageText.setText(message);
        creatorText.setText(creator);
        postTimeText.setText(date);

    }

}

