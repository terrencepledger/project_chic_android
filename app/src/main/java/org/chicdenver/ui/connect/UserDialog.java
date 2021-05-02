package org.chicdenver.ui.connect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;

import androidx.fragment.app.DialogFragment;

import org.chicdenver.R;

public class UserDialog extends DialogFragment {

    private final UserListAdapter adapter;
    private DialogListener listener = null;

    public UserDialog(UserListAdapter adapter)
    {

        this.adapter = adapter;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_user)
                .setItems(adapter.getItems(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(listener != null)
                                    listener.onSelection(dialogInterface,
                                            adapter.userList.get(i).get("Name").toString(),
                                            adapter.userList.get(i).get("ID").toString());

                            }
                        }
                );

        return builder.create();

    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    public interface DialogListener {
        void onSelection(DialogInterface dialog, String userName, String userId);
    }

}
