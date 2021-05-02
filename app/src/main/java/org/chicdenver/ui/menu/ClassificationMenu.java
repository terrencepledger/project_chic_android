package org.chicdenver.ui.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.chicdenver.R;
import org.chicdenver.data.model.Classification;
import org.chicdenver.data.model.LoggedInUser;
import org.chicdenver.ui.main_pages.AccountActivity;
import org.chicdenver.ui.main_pages.AnnouncementsActivity;
import org.chicdenver.ui.main_pages.HomeActivity;

import java.io.Serializable;
import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ClassificationMenu {

    protected Activity theActivity;
    private Drawer drawer;
    private AccountHeader header;
    protected Classification classification;

    public ClassificationMenu(Activity activity, LoggedInUser loggedInUser, Toolbar toolbar) {

        theActivity = activity;
        classification = null;

        initBaseItems(loggedInUser, toolbar);

    }

    private void initBaseItems(LoggedInUser loggedInUser, Toolbar toolbar) {

        PrimaryDrawerItem homeItem = new PrimaryDrawerItem()
                .withName(theActivity.getString(R.string.menu_home)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_home_id))).withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                navigate(theActivity, HomeActivity.class, drawerItem.getIdentifier());
                                return true;
                            }
                        }
                );
        SecondaryDrawerItem accountItem = new SecondaryDrawerItem()
                .withName(theActivity.getString(R.string.menu_account)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_account_id))).withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                navigate(theActivity, AccountActivity.class, drawerItem.getIdentifier());
                                return true;
                            }
                        }
                );
        SecondaryDrawerItem announcementsItem = new SecondaryDrawerItem()
                .withName(theActivity.getString(R.string.menu_announcements)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_announcements_id))).withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                navigate(theActivity, AnnouncementsActivity.class, drawerItem.getIdentifier());
                                return true;
                            }
                        }
                );

        header = new AccountHeaderBuilder()
                .withActivity(theActivity)
                .withHeaderBackground(R.color.colorAccent)
                .build();
        ProfileDrawerItem profileDrawerItem =
                new ProfileDrawerItem().withName(loggedInUser.getDisplayName(1))
                        .withEmail(loggedInUser.getUserEmail())
                        .withIcon(theActivity.getDrawable(R.drawable.placeholder_avatar))
                        .withIdentifier(Long.parseLong(theActivity.getString(R.string.profileItem)));
        Log.d("Identifier", String.valueOf(profileDrawerItem.getIdentifier()));
        header.addProfile(profileDrawerItem, 0);
        header.setActiveProfile(profileDrawerItem);

        ArrayList<IDrawerItem> items = new ArrayList<>();

        items.add(accountItem);
        items.add(announcementsItem);

        drawer = new DrawerBuilder().addDrawerItems(homeItem).withActivity(theActivity)
                .withAccountHeader(header).withToolbar(toolbar)
                .build();

        addItems(items);

    }

    public static void navigate(Activity theActivity, Class destination, Long pos)
    {

        Intent myIntent = new Intent(theActivity, destination);
        myIntent.setFlags(FLAG_ACTIVITY_CLEAR_TASK);
//        theActivity.finish();
        myIntent.putExtra("ITEM_POS", pos);
        theActivity.startActivity(myIntent);

    }

    public static void navigate(Activity theActivity, Class destination)
    {

        Intent myIntent = new Intent(theActivity, destination);
        theActivity.startActivity(myIntent);

    }

    public static void welcome(String name, Context context) {

        Toast.makeText(context, String.format("%s %s!", context.getString(R.string.welcome), name),
                Toast.LENGTH_SHORT).show();

    }

    public void addItems(ArrayList<IDrawerItem> extraItems) {

        extraItems.forEach(secondaryDrawerItem -> { drawer.addItem(secondaryDrawerItem); });

    }

    public Drawer getDrawer() {
        return drawer;
    }

    public AccountHeader getHeader() {
        return header;
    }

}
