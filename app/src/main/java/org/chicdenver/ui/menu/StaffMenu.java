package org.chicdenver.ui.menu;

import android.app.Activity;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.chicdenver.R;
import org.chicdenver.ui.staff_pages.ManageAnnouncementsActivity;
import org.chicdenver.ui.staff_pages.ManageClassesActivity;
import org.chicdenver.ui.staff_pages.ManageStudentsActivity;

public class StaffMenu extends ExtraMenuItems {

    public StaffMenu(Activity activity) {

        super(activity);

    }

    @Override
    protected void initExtraItems() {

//        SecondaryDrawerItem allStudentsItem = new SecondaryDrawerItem()
//                .withName(theActivity.getString(R.string.menu_staff_all_students)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_staff_all_students_id))).withOnDrawerItemClickListener(
//                        new Drawer.OnDrawerItemClickListener() {
//                            @Override
//                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                                ClassificationMenu.navigate(theActivity, ManageStudentsActivity.class, drawerItem.getIdentifier());
//                                return false;
//                            }
//                        }
//                );
//        SecondaryDrawerItem assignmentsItem = new SecondaryDrawerItem()
//                .withName(theActivity.getString(R.string.menu_staff_classes)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_staff_classes_id))).withOnDrawerItemClickListener(
//                        new Drawer.OnDrawerItemClickListener() {
//                            @Override
//                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                                ClassificationMenu.navigate(theActivity, ManageClassesActivity.class, drawerItem.getIdentifier());
//                                return false;
//                            }
//                        }
//                );
//        SecondaryDrawerItem announcementsItem = new SecondaryDrawerItem()
//                .withName(theActivity.getString(R.string.menu_staff_all_announcements))
//                .withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_staff_all_announcements_id)))
//                .withOnDrawerItemClickListener(
//                        new Drawer.OnDrawerItemClickListener() {
//                            @Override
//                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                                ClassificationMenu.navigate(theActivity, ManageAnnouncementsActivity.class, drawerItem.getIdentifier());
//                                return false;
//                            }
//                        }
//                );
//
//
//        items.add(allStudentsItem);
//        items.add(assignmentsItem);
//        items.add(announcementsItem);

    }

}
