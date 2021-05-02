package org.chicdenver.ui.menu;

import android.app.Activity;
import android.view.View;


import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.chicdenver.R;
import org.chicdenver.ui.wf_pages.FormsActivity;

public class WFMenu extends ExtraMenuItems {

    public WFMenu(Activity activity) {

        super(activity);

    }

    @Override
    protected void initExtraItems() {

//        SecondaryDrawerItem jpItem = new SecondaryDrawerItem()
//                .withName(theActivity.getString(R.string.menu_wf_jp)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_wf_jp_id))).withOnDrawerItemClickListener(
//                        new Drawer.OnDrawerItemClickListener() {
//                            @Override
//                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
////                                Classification.navigate(theActivity, HomeActivity.class, drawerItem);
//                                return false;
//                            }
//                        }
//                );
        SecondaryDrawerItem formsItem = new SecondaryDrawerItem()
                .withName(theActivity.getString(R.string.menu_wf_forms)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_wf_forms_id))).withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                ClassificationMenu.navigate(theActivity, FormsActivity.class);
                                return false;
                            }
                        }
                );

//        items.add(jpItem);
        items.add(formsItem);

    }

}
