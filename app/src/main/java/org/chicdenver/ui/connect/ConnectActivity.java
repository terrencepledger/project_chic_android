package org.chicdenver.ui.connect;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.chicdenver.R;
import org.chicdenver.data.model.ChicAssignments.ChicCourse;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.data.model.Messages.ConnectActivityFragmentAdapter;
import org.chicdenver.data.model.Messages.ConnectionChannel;
import org.chicdenver.data.model.Messages.ConnectionChannelFragment;
import org.chicdenver.ui.DrawerActivity;
import org.chicdenver.ui.menu.ClassificationMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;


public class ConnectActivity extends DrawerActivity {

    private final ArrayList<HashMap> users = new ArrayList<>();
    private ViewPager2 connectActivityPager;
    private Classification mClassification;

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        activity = this;

        connectActivityPager = findViewById(R.id.connectActivityPager);

        init();

    }

    protected void setUserListener() {

        loggedInUser.setListener(new LoggedInUser.UserDatabaseListener() {
            @Override
            public void onImageLoaded(Drawable profileImage) {
                menu.updateProfile(profileImage);
            }

            @Override
            public void onUserInfoLoaded(String name, Classification classification, String number) {
                menu.updateProfile(name);
                menu.updateMenuItems(classification);
                mClassification = classification;
                if (classification.equals(Classification.Staff))
                {
                    collectAllUsers();
                    loggedInUser.startStaffLoad();
                }
                else {
                    collectGroupUsers();
                    loggedInUser.startStudentLoad();
                }
            }

            @Override
            public void onClassroomLoaded() {
                if(mClassification.equals(Classification.Staff))
                    populateStaffMessages();
                else
                    populateMessages();
            }

        });

    }

    private void populateStaffMessages() {

        ArrayList<String> courseIds = new ArrayList<>();
        ArrayList<String> classNames = new ArrayList<>();

        loggedInUser.mStaff.getAllClassrooms().forEach(
                new Consumer<ChicCourse>() {
                    @Override
                    public void accept(ChicCourse course) {
                        classNames.add(course.getCourseName());
                        courseIds.add(course.getCourseId());
                    }
                }
        );

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {

                        TabLayout.Tab peerConnectionTab = tabLayout.newTab();
                        peerConnectionTab.setText("All Messages");
                        tabLayout.addTab(peerConnectionTab); // add  the tab at in the TabLayout

                        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

                        ArrayList<ConnectionChannel> studentChannels = new ArrayList<>();
                        ArrayList<ConnectionChannel> classChannels = new ArrayList<>();
                        ArrayList<String> keyList = new ArrayList<>();
                        ArrayList<String> classKeys = new ArrayList<>();

                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Channels")
                                .getRef();

                        dbRef.orderByKey().addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                                            @Override
                                            public void accept(DataSnapshot dataSnapshot) {
                                                ConnectionChannel current =
                                                        dataSnapshot.getValue(ConnectionChannel.class);
                                                if (current.getMemberIDs().contains(getString(R.string.connection_staff_id))) {
                                                    if(courseIds.contains(current.getChannelId())) {
                                                        classChannels.add(current);
                                                        classKeys.add(dataSnapshot.getKey());
                                                    }
                                                    else if (current.getMessages().size() != 0) {
                                                        studentChannels.add(current);
                                                        keyList.add(dataSnapshot.getKey());
                                                    }
                                                }
                                            }
                                        });

                                        if(classChannels.size() < courseIds.size()) {
                                            for (ConnectionChannel channel: classChannels) {
                                                courseIds.remove(channel.getChannelId());
                                            }
                                            for(String id: courseIds) {
                                                ConnectionChannel courseChannel = new ConnectionChannel();
                                                courseChannel.setChannelId(id);
                                                courseChannel.addMember(getString(R.string.connection_staff_title),
                                                        getString(R.string.connection_staff_id));

                                                DatabaseReference newRef = FirebaseDatabase.getInstance().getReference().child("Channels")
                                                        .push().getRef();
                                                newRef.setValue(courseChannel);

                                                classChannels.add(courseChannel);
                                                classKeys.add(newRef.getKey());
                                            }
                                        }

                                        ConnectActivityFragmentAdapter adapter = new ConnectActivityFragmentAdapter(
                                                activity, studentChannels, keyList, classNames, classChannels,
                                                classKeys, loggedInUser.getDisplayName(0));

                                        connectActivityPager.setAdapter(adapter);
                                        connectActivityPager.setUserInputEnabled(false);

                                        new TabLayoutMediator(tabLayout, connectActivityPager,
                                                new TabLayoutMediator.TabConfigurationStrategy() {
                                                    @Override
                                                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                                        if(position != 0) {
                                                            position--;
                                                            tab.setText(classNames.get(position));
                                                        }
                                                        else
                                                            tab.setText("All Messages");
                                                    }
                                                }).attach();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                }
                        );

                    }
                }
        );

    }


    @Override
    public void updateUi() {

    }

    public void collectAllUsers() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        task.getResult().forEach(
                                new Consumer<QueryDocumentSnapshot>() {
                                    @Override
                                    public void accept(QueryDocumentSnapshot queryDocumentSnapshot) {
                                        if(!queryDocumentSnapshot.get("Classification", Classification.class)
                                                .equals(Classification.Unverified) &&
                                                !queryDocumentSnapshot.getId().equals(loggedInUser.getUid())
                                        ) {
                                            HashMap user = (HashMap) queryDocumentSnapshot.getData();
                                            user.put("ID", queryDocumentSnapshot.getId());
                                            users.add(user);
                                        }
                                    }
                                }
                        );
                    }
                }
        );

    }

    public void collectGroupUsers() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(loggedInUser.user.getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentReference groupRef = task.getResult().get("Group", DocumentReference.class);

                        groupRef.get().addOnCompleteListener(

                                new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        for(HashMap user : (ArrayList<HashMap>) task.getResult()
                                                .get("Students"))
                                        {

                                            if(!user.containsValue(loggedInUser.getUid()))
                                                users.add(user);

                                        }


                                    }
                                }

                        );

                    }
                }
        );

    }

    public void populateMessages() {

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                TabLayout.Tab peerConnectionTab = tabLayout.newTab();
//                peerConnectionTab.setText(getString(R.string.connection_peers_title));
//                tabLayout.addTab(peerConnectionTab); // add  the tab at in the TabLayout

                TabLayout.Tab classConnectionTab = tabLayout.newTab();
                classConnectionTab.setText(getString(R.string.connection_class_title));
                tabLayout.addTab(classConnectionTab); // add  the tab  in the TabLayout

                TabLayout.Tab chicConnectionTab = tabLayout.newTab();
                chicConnectionTab.setText(getString(R.string.connection_staff_title));
                tabLayout.addTab(chicConnectionTab); // add  the tab at in the TabLayout

//                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            }
        });

        ArrayList<ConnectionChannel> channels = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        final ConnectionChannel[] classChannel = new ConnectionChannel[1];
        final String[] classKey = new String[1];
        final ConnectionChannel[] teacherChannel = new ConnectionChannel[1];
        final String[] teacherKey = new String[1];

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Channels")
                .getRef();

        dbRef.orderByKey().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                            @Override
                            public void accept(DataSnapshot dataSnapshot) {
                                ConnectionChannel current =
                                        dataSnapshot.getValue(ConnectionChannel.class);
                                if (current.getMemberIDs().contains(loggedInUser.getUid())) {

                                    if(current.getChannelId().contains(
                                            loggedInUser.getStudentClassroom().getCourseId())) {
                                        classChannel[0] = current;
                                        classKey[0] = dataSnapshot.getKey();
                                    }
                                    else if(current.getChannelId().contains(getString(R.string.staff_channel_id)))
                                    {
                                        teacherChannel[0] = current;
                                        teacherKey[0] = dataSnapshot.getKey();
                                    }
                                    else {
                                        channels.add(current);
                                        keys.add(dataSnapshot.getKey());
                                    }

                                }
                            }
                        });

                        if(teacherChannel[0] == null)
                        {
                            teacherChannel[0] = new ConnectionChannel();
                            teacherChannel[0].setChannelId(ConnectionChannel.createChannelId(getString(R.string.connection_staff_id),
                                    loggedInUser.getUid()));
                            teacherChannel[0].addMember(loggedInUser);
                            teacherChannel[0].addMember(getString(R.string.connection_staff_title), getString(R.string.connection_staff_id));

                            DatabaseReference newRef = dbRef.push().getRef();
                            newRef.setValue(teacherChannel[0]);
                            teacherKey[0] = newRef.getKey();
                        }
                        if(classChannel[0] == null)
                        {
                            snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                                @Override
                                public void accept(DataSnapshot dataSnapshot) {
                                    ConnectionChannel current =
                                            dataSnapshot.getValue(ConnectionChannel.class);
                                    if (current.getChannelId().equals(loggedInUser.getStudentClassroom().getCourseId())) {
                                        current.addMember(loggedInUser);
                                        dbRef.child(dataSnapshot.getKey()).setValue(current);
                                        classChannel[0] = current;
                                        classKey[0] = dataSnapshot.getKey();
                                    }
                                }
                            });
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ConnectActivityFragmentAdapter adapter = new ConnectActivityFragmentAdapter(activity,
                                        channels, keys, loggedInUser.getDisplayName(0),
                                        classChannel[0], classKey[0], teacherChannel[0], teacherKey[0]);

                                connectActivityPager.setAdapter(adapter);
                                connectActivityPager.setUserInputEnabled(false);

                                tabLayout.addOnTabSelectedListener(

                                        new TabLayout.OnTabSelectedListener() {
                                            @Override
                                            public void onTabSelected(TabLayout.Tab tab) {
                                                connectActivityPager.setCurrentItem(tab.getPosition());
                                            }

                                            @Override
                                            public void onTabUnselected(TabLayout.Tab tab) {

                                            }

                                            @Override
                                            public void onTabReselected(TabLayout.Tab tab) {
                                                connectActivityPager.setCurrentItem(tab.getPosition());
                                            }

                                        }
                                );

                            }
                        });



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                }
        );

    }

//    public void showPopup(Classification mClassification) {
//
//        UserDialog dialog = new UserDialog(new UserListAdapter(users, this));
//
//        dialog.setListener(
//
//                new UserDialog.DialogListener() {
//
//                    @Override
//                    public void onSelection(DialogInterface dialog, String userName, String userID) {
//
//                        final ConnectionChannel[] userChannel = new ConnectionChannel[1];
//                        String[] userKey = new String[1];
//
//                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
//                                .child("Channels").getRef();
//
//                        dbRef.orderByKey().addListenerForSingleValueEvent(
//                                new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                        snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
//                                            @Override
//                                            public void accept(DataSnapshot dataSnapshot) {
//                                                ConnectionChannel current =
//                                                        dataSnapshot.getValue(ConnectionChannel.class);
//                                                if (mClassification.equals(Classification.Staff)) {
//                                                    String staffConnectionId = getString(R.string.connection_staff_id);
//                                                    if (current.getMemberIDs().contains(staffConnectionId)
//                                                            && current.getMemberIDs().contains(userID)) {
//                                                        userChannel[0] = current;
//                                                        userKey[0] = dataSnapshot.getKey();
//                                                    }
//                                                } else if (current.getChannelId().equals(
//                                                        ConnectionChannel.createChannelId(loggedInUser.getUid(),
//                                                                userID)
//                                                )) {
//                                                    userChannel[0] = current;
//                                                    userKey[0] = dataSnapshot.getKey();
//                                                }
//                                            }
//                                        });
//
//                                        if(userChannel[0] == null) {
//                                            if (mClassification.equals(Classification.Staff)) {
//                                                userChannel[0] = new ConnectionChannel();
//                                                userChannel[0].setChannelId(ConnectionChannel.createChannelId(getString(R.string.connection_staff_id),
//                                                        userID));
//                                                userChannel[0].addMember(userName, userID);
//                                                userChannel[0].addMember(getString(R.string.connection_staff_title), getString(R.string.connection_staff_id));
//                                            }
//                                            else {
//                                                userChannel[0] = new ConnectionChannel();
//                                                userChannel[0].setChannelId(ConnectionChannel.createChannelId(loggedInUser.getUid(),
//                                                        userID));
//                                                userChannel[0].addMember(userName, userID);
//                                                userChannel[0].addMember(loggedInUser);
//                                            }
//
//                                            DatabaseReference newRef = dbRef.push().getRef();
//                                            newRef.setValue(userChannel[0]);
//                                            userKey[0] = newRef.getKey();
//                                        }
//
//                                        Fragment connectionFragment =
//                                                ConnectionChannelFragment.newInstance(
//                                                        (ArrayList<ConnectionChannel.ChannelMessage>) userChannel[0].getMessages(),
//                                                        userChannel[0].getChannelHeader(loggedInUser.getDisplayName(1)),
//                                                        userChannel[0].getChannelId(), loggedInUser.getDisplayName(0),
//                                                        userKey[0]
//                                                );
//
//                                        FragmentManager manager = getSupportFragmentManager();
//                                        FragmentTransaction transaction = manager.beginTransaction();
//                                        transaction.replace(R.id.root,
//                                                connectionFragment,"Connection");
//                                        transaction.addToBackStack(null);
//                                        transaction.commit();
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//
//                                });
//                    }
//                });
//
//        dialog.show(getSupportFragmentManager(), "User List");
//
//    }

}