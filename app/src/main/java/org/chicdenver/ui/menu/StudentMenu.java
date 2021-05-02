package org.chicdenver.ui.menu;

import android.app.Activity;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.chicdenver.R;
import org.chicdenver.ui.student_pages.ClassPortalActivity;

public class StudentMenu extends ExtraMenuItems {

    public StudentMenu(Activity activity) {

        super(activity);

    }

    @Override
    protected void initExtraItems() {

        SecondaryDrawerItem acItem = new SecondaryDrawerItem()
                .withName(theActivity.getString(R.string.menu_students_ac)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_students_ac_id))).withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                                ClassificationMenu.navigate(theActivity, ActivityCenter.class, position);
                                return false;
                            }
                        }
                );
        SecondaryDrawerItem cpItem = new SecondaryDrawerItem()
                .withName(theActivity.getString(R.string.menu_students_cp)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_students_cp_id)))
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                ClassificationMenu.navigate(theActivity, ClassPortalActivity.class, drawerItem.getIdentifier());
                                return true;
                            }
                        }
                );

        items.add(cpItem);

    }

}
