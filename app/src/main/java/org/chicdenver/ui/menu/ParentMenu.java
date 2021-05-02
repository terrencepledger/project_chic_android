package org.chicdenver.ui.menu;

import android.app.Activity;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.chicdenver.R;

public class  ParentMenu extends ExtraMenuItems{

    public ParentMenu(Activity activity) {

        super(activity);

    }

    protected void initExtraItems() {

        SecondaryDrawerItem childrenItem = new SecondaryDrawerItem().withName(theActivity.getString(R.string.menu_parents_children)).withIdentifier(Long.parseLong(theActivity.getString(R.string.menu_parents_children_id)))
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                                navigate(activity, ClassPortalActivity.class, drawerItem);
                                return false;
                            }
                        }
                );

        items.add(childrenItem);

    }

}
