package org.chicdenver.ui.menu;

import android.app.Activity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.chicdenver.R;
import org.chicdenver.ui.login.LoginActivity;
import org.chicdenver.ui.connect.ConnectActivity;

import java.util.ArrayList;

public abstract class ExtraMenuItems {

    Activity theActivity;

    protected ArrayList<IDrawerItem> items = new ArrayList<>();

    public ExtraMenuItems(Activity activity) {

        theActivity = activity;

        initExtraItems();

    }

    protected abstract void initExtraItems();

    public static ArrayList<IDrawerItem> getBottomItems(Activity theActivity, FirebaseAuth auth) {

        ArrayList<IDrawerItem> items = new ArrayList<>();

        SecondaryDrawerItem settingsItem = new SecondaryDrawerItem().withName(theActivity.getString(R.string.menu_settings)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_settings_id)))
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                        navigate(ClassPortalActivity.class, drawerItem);
                                return true;
                            }
                        }
                );

        SecondaryDrawerItem logoutItem = new SecondaryDrawerItem().withName(theActivity.getString(R.string.action_exit))
                .withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_logout_id)))
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                auth.signOut();
                                ClassificationMenu.navigate(theActivity, LoginActivity.class);
                                return false;
                            }
                        }
                );

        SecondaryDrawerItem connectItem = new SecondaryDrawerItem().withName(theActivity.getString(R.string.title_activity_connect))
                .withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_connect_id)))
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                ClassificationMenu.navigate(theActivity, ConnectActivity.class, drawerItem.getIdentifier());
                                return true;
                            }
                        }
                );

        items.add(connectItem);
//        items.add(settingsItem);
        items.add(logoutItem);

        return items;

    }

    public ArrayList<IDrawerItem> getItems() {
        return items;
    }

}
