package org.chicdenver.ui.menu;

import android.app.Activity;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.chicdenver.R;


import static org.chicdenver.ui.menu.ClassificationMenu.navigate;

public class UnverifiedUserMenu extends ExtraMenuItems{

    public UnverifiedUserMenu(Activity activity) {

        super(activity);

    }

    protected void initExtraItems() {

        SecondaryDrawerItem childrenItem = new SecondaryDrawerItem().withName(theActivity.getString(R.string.menu_unverified))
                .withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_unverified_id)))
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                navigate(theActivity, UnverifiedUserMenu.class, drawerItem.getIdentifier());
                                return false;
                            }
                        }
                );

        items.add(childrenItem);

    }

}