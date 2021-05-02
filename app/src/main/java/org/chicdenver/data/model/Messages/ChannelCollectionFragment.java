package org.chicdenver.data.model.Messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.chicdenver.R;
import org.chicdenver.data.model.LoggedInUser;

import java.util.ArrayList;

public class ChannelCollectionFragment extends Fragment {

    private ArrayList<String> keys;

    public static ChannelCollectionFragment newInstance(ArrayList<ConnectionChannel> channelList,
                                                        ArrayList<String> keys, String username) {

        ChannelCollectionFragment myFragment = new ChannelCollectionFragment();

        Bundle args = new Bundle();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> lastMessages = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
//        ArrayList<String> icons = new ArrayList<>();

        for (ConnectionChannel channel: channelList
             ) {
            if(channel.getMessages().size() == 0)
                break;
            names.add(channel.getChannelHeader(username));
            lastMessages.add(channel.getLastMessage(username).getMessage());
            dates.add(channel.getLastMessage(username).getPrettyPostTime());
        }

        args.putStringArrayList("Names", names);
        args.putStringArrayList("LastMessages", lastMessages);
        args.putStringArrayList("Dates", dates);
        args.putString("UserName", username);
        myFragment.setList(keys);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.connection_all_messages, container, false);
    }

    public void setList(ArrayList<String> keys)
    {
        this.keys = keys;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ChannelHeaderAdapter adapter = new ChannelHeaderAdapter(getArguments(), keys);
        ((RecyclerView) view.findViewById(R.id.messageHeadersRecycler))
                .setAdapter(adapter);
    }
}


