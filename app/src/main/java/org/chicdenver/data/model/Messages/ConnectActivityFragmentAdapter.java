package org.chicdenver.data.model.Messages;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.chicdenver.data.model.LoggedInUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ConnectActivityFragmentAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> frags;

    public ConnectActivityFragmentAdapter(Activity activity, ArrayList<ConnectionChannel> channels,
                                          ArrayList<String> keyList, String username,
                                          ConnectionChannel classChannel, String classKey,
                                          ConnectionChannel teacherChannel, String teacherKey) {
        super((FragmentActivity) activity);
        frags = new ArrayList<>(Arrays.asList(
//                ChannelCollectionFragment.newInstance(channels, keyList, username),
                ConnectionChannelFragment.newInstance((ArrayList<ConnectionChannel.ChannelMessage>) classChannel.getMessages(),
                        "Class Chat", classChannel.getChannelId(), username, classKey),
                ConnectionChannelFragment.newInstance((ArrayList<ConnectionChannel.ChannelMessage>) teacherChannel.getMessages(),
                        "Teacher Chat", teacherChannel.getChannelId(), username, teacherKey)
        ));
    }

    public ConnectActivityFragmentAdapter(Activity activity, ArrayList<ConnectionChannel> allChannels,
                                          ArrayList<String> keyList, ArrayList<String> classNames,
                                          ArrayList<ConnectionChannel> classChannels,
                                          ArrayList<String> classKeys,
                                          String username) {
        super((FragmentActivity) activity);
        frags = new ArrayList<>(Collections.singletonList(
                ChannelCollectionFragment.newInstance(allChannels, keyList, username)
        ));
        for (int i = 0; i < classChannels.size(); i++) {

            frags.add(ConnectionChannelFragment.newInstance(
                    (ArrayList<ConnectionChannel.ChannelMessage>) classChannels.get(i).getMessages(),
                    classNames.get(i), classChannels.get(i).getChannelId(), username, classKeys.get(i)));

        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return frags.get(position);
    }

    @Override
    public int getItemCount() {
        return frags.size();
    }

}
