package org.chicdenver.data.model.Messages;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.chicdenver.R;
import org.chicdenver.ui.connect.ConnectActivity;
import org.chicdenver.ui.menu.ClassificationMenu;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class ConnectionChannelFragmentHolder {

    private final MessageAdapter adapter;
    ViewGroup container;

    TextView chatMemberName;
    Button backButton;
    Button sendMessageButton;
    EditText inputMessageEditText;

    public ConnectionChannelFragmentHolder (View view, MessageAdapter adapter, String channelID, String userName) {

        this.chatMemberName = view.findViewById(R.id.channelHeaderNameText);
        this.backButton = view.findViewById(R.id.channelBackButton);
        this.sendMessageButton = view.findViewById(R.id.channelSubmitButton);
        this.inputMessageEditText = view.findViewById(R.id.inputMessageEditText);
        this.adapter = adapter;
        this.container = (ViewGroup) view;

        setSubmitButton(channelID, userName);

        backButton.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                            ClassificationMenu.navigate((Activity) view.getContext(),
                                    ConnectActivity.class);

                    }
                }

        );

    }

    public void setChannelName(String channelName) {

        chatMemberName.setText(channelName);

    }

    public void setChannelImage(String uid) {

        String path = String.format("images/%s.jpg", uid);
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child(path);
        File imageFile = null;
        try {
            imageFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            Log.e("File-Creation-Error", e.getMessage());
        }

        Uri fileUri = Uri.fromFile(imageFile);

        profileImageRef.getFile(fileUri).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                Drawable temp;
                if (task.isSuccessful())
                    temp = Drawable.createFromPath(fileUri.getPath());
                else {
                    temp = container.getContext().getDrawable(R.drawable.placeholder_avatar);
                    Log.e("Image download error", String.valueOf(task.getException()));
                }
            }
        });

    }

    private void setSubmitButton(String channelID, String user) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("Channels").getRef();

        sendMessageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String message = inputMessageEditText.getText().toString();

                        inputMessageEditText.setText("");

                        dbRef.orderByKey().addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                                            @Override
                                            public void accept(DataSnapshot dataSnapshot) {

                                                ConnectionChannel current =
                                                        dataSnapshot.getValue(ConnectionChannel.class);


                                                if(current.getChannelId().equals(channelID)) {
                                                    current.addMessage(user, message);
                                                    dbRef.child(dataSnapshot.getKey())
                                                            .setValue(current);
                                                    adapter.add(message, user,
                                                            current.getMessages().get(
                                                                    current.getMessages().size() - 1)
                                                                    .getPrettyPostTime()
                                                    );
                                                }
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                }
                        );
                    }});

    }

}
