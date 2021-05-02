package org.chicdenver.ui.main_pages;

import android.os.Bundle;

import org.chicdenver.R;
import org.chicdenver.ui.DrawerActivity;

public class UnverifiedUserActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unverified_user);

        activity = this;

        init();

    }

    @Override
    public void updateUi() {



    }
}