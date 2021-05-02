package org.chicdenver.data.model.Documents;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WfDoc {

    public enum DocStatus {
        Submitted,
        Reviewed,
    }

    LoggedInUser user;
    String title;
    String date;
    String status;
    String downloadLink;
    String userComments;
    String staffComments;
    WfDoc self = this;
    public DocListener listener;

    public WfDoc(LoggedInUser user, DocListener listener, String title, long date, String downloadLink)
    {
        this.user = user;
        this.title = title;
        this.date = String.valueOf(date);
        this.downloadLink = downloadLink;
        this.listener = listener;


        FirebaseFirestore.getInstance().collection("Wf-Docs").document(user.getUid())
                .get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d("Doc", title + " : " + date + " : " + downloadLink);
//                        String[] split = title.split(user.getDisplayName(1) + "-");
//                        split.remove(0);
                        String filename = title.split("\\.")[0];
                        Log.d("Task", task.getResult().getData().toString());
                        HashMap obj = (HashMap) task.getResult().get(filename);
                        status = obj.get("Status").toString();
                        Log.d("status", obj.get("Status").toString());
                        staffComments = obj.get("StaffComments").toString();
                        userComments = obj.get("UserComments").toString();
                        listener.onReady(self);

                    }
                }
        );

    }

    public interface DocListener {

        void onReady(WfDoc doc);

    }

}
