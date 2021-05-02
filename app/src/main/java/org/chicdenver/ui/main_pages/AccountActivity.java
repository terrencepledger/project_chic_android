package org.chicdenver.ui.main_pages;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;

import org.chicdenver.R;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.ui.DrawerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountActivity extends DrawerActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode == Activity.RESULT_OK) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                // Copying
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();

                setProfileImage(Drawable.createFromPath(photoFile.getAbsolutePath()));
                Log.d("File exists: ", String.valueOf(photoFile.exists()));
                updateProfileImage(photoFile);

            } catch (IOException ex) {
                Log.d("Image creation error", "Error occurred while creating the file");
            }


        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        activity = this;

        init();

        connectButtons();

    }

    @Override
    public void updateUi() {

        loadAccountInfo();

    }

    @Override
    protected void setUserListener() {

        loggedInUser.setListener(new LoggedInUser.UserDatabaseListener() {
            @Override
            public void onImageLoaded(Drawable profileImage) {
                menu.updateProfile(profileImage);
                setProfileImage(profileImage);
            }

            @Override
            public void onUserInfoLoaded(String name, Classification classification, String number) {

                menu.updateProfile(name);

                setName(name);
                setNumber(number);

                menu.updateMenuItems(classification);

            }

            @Override
            public void onClassroomLoaded() {

            }

        });

    }

    private void connectButtons() {

        Button updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((TextInputLayout) findViewById(R.id.displayName)).getEditText().getText().toString();
                updateName(name);
            }
        });

        ImageView profileImage = findViewById(R.id.accountImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(
                        new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
                        ),
                        1
                );

            }
        });

    }

    private void loadAccountInfo() {

        setName(loggedInUser.getDisplayName(1));
        setProfileImage(getDrawable(R.drawable.placeholder_avatar));

    }

    private void setName(String displayName) {

        TextInputLayout text = findViewById(R.id.displayName);
        text.getEditText().setText(displayName);

    }

    private void setNumber(String number) {

        TextInputLayout text = findViewById(R.id.phoneNumber);
        text.getEditText().setText(number);

    }

    private void setProfileImage(Drawable drawable) {

        ImageView imageView = findViewById(R.id.accountImage);
        imageView.setImageDrawable(drawable);

    }

    private void updateProfileImage(File image) {

        loggedInUser.updateProfileImage(image);

    }

    private void updateName(String name) {

        loggedInUser.updateName(name);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

}