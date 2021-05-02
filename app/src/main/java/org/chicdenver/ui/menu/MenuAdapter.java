package org.chicdenver.ui.menu;

import android.app.Activity;

import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;


import com.google.firebase.auth.FirebaseAuth;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.chicdenver.R;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.ui.main_pages.UnverifiedUserActivity;

import java.util.ArrayList;


public class MenuAdapter {

    private Activity activity;
    private LoggedInUser loggedInUser;
    private ClassificationMenu menu;

    private long INDICATOR;

    public MenuAdapter(Activity activity, LoggedInUser loggedInUser, Toolbar toolbar) {

        this.activity = activity;
        this.loggedInUser = loggedInUser;

        initMenu(toolbar);

    }

    public void initMenu(Toolbar toolbar) {

        menu = new ClassificationMenu(activity, loggedInUser, toolbar);

    }

    public void updateMenuItems(Classification classification) {

        if(classification.equals(this.menu.classification))
            return;

        ArrayList<IDrawerItem> extraItems;

        FirebaseAuth auth = FirebaseAuth.getInstance();

        switch(classification)
        {
            case Parent:
                extraItems = new ParentMenu(activity).getItems();
                break;
            case Workforce:
                extraItems = new WFMenu(activity).getItems();
                break;
            case Staff:
                extraItems = new StaffMenu(activity).getItems();
                menu.getDrawer().removeItem(Long.parseLong(activity.getString(R.string.menu_announcements_id)));
                break;
            case Student:
                extraItems = new StudentMenu(activity).getItems();
                break;
            default:
                extraItems = new UnverifiedUserMenu(activity).getItems();
                menu.getDrawer().removeItem(Long.parseLong(activity.getString(R.string.menu_home_id)));
                menu.getDrawer().removeItem(Long.parseLong(activity.getString(R.string.menu_announcements_id)));
                menu.getDrawer().removeItem(Long.parseLong(activity.getString(R.string.menu_connect_id)));
                if(activity.getTitle().toString().equals(activity.getString(R.string.menu_home)))
                    ClassificationMenu.navigate(activity, UnverifiedUserActivity.class,
                            Long.parseLong(activity.getString(R.string.menu_unverified_id)));
                break;

        }

        menu.addItems(extraItems);
        menu.addItems(ExtraMenuItems.getBottomItems(activity, auth));
        menu.classification = classification;

        menu.getDrawer().setSelection(INDICATOR, false);

    }

    public void updateProfile(Drawable profileImage) {

        ProfileDrawerItem profileItem = (ProfileDrawerItem) menu.getHeader()
                .getActiveProfile().withIcon(profileImage);

        menu.getHeader().updateProfile(profileItem);

    }

    public void updateProfile(String name) {

        ProfileDrawerItem profileItem = (ProfileDrawerItem) menu.getHeader()
                .getActiveProfile().withName(name);

        menu.getHeader().updateProfile(profileItem);

    }

    public void setIndicator(Long identifier) {
        INDICATOR = identifier;
    }

}
