package org.chicdenver.data.model.Messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.chicdenver.R;
import org.chicdenver.data.model.LoggedInUser;

import java.util.ArrayList;

public class ConnectionChannelFragment extends Fragment {

    public static ConnectionChannelFragment newInstance(
            ArrayList<ConnectionChannel.ChannelMessage> channelMessages,
            String channelName, String channelID, String userName, String dbKey) {

        ConnectionChannelFragment myFragment = new ConnectionChannelFragment();

        Bundle args = new Bundle();
        ArrayList<String> messageTexts = new ArrayList<>();
        ArrayList<String> creatorTexts = new ArrayList<>();
        ArrayList<String> dateTexts = new ArrayList<>();

        for (ConnectionChannel.ChannelMessage channelMessage: channelMessages
        ) {
            messageTexts.add(channelMessage.getMessage());
            creatorTexts.add(channelMessage.getCreator());
            dateTexts.add(channelMessage.getPrettyPostTime());
        }

        args.putStringArrayList("MessageTexts", messageTexts);
        args.putStringArrayList("CreatorTexts", creatorTexts);
        args.putStringArrayList("DateTexts", dateTexts);
        args.putString("ChannelName", channelName);
        args.putString("ChannelID", channelID);
        args.putString("UserName", userName);
        args.putString("Key", dbKey);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.connection_channel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MessageAdapter adapter = new MessageAdapter(getArguments());
        FirebaseDatabase.getInstance().getReference().child("Channels").child(getArguments().getString("Key"))
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                adapter.update(snapshot.getValue(ConnectionChannel.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
        ((RecyclerView) view.findViewById(R.id.connectionChannelRecycler))
                .setAdapter(adapter);
        ConnectionChannelFragmentHolder holder = new ConnectionChannelFragmentHolder(view, adapter,
                getArguments().getString("ChannelID"), getArguments().getString("UserName"));
        holder.setChannelName(getArguments().getString("ChannelName"));
//        holder.setChannelImage();
    }

}
