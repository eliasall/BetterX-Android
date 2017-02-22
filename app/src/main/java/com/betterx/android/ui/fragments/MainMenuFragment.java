package com.betterx.android.ui.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.betterx.android.R;
import com.betterx.android.app.events.MessageReceivedEvent;
import com.betterx.android.classes.MainThreadBus;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.ui.adapters.MainMenuAdapter;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class MainMenuFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";


    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelectionChanged(int from, int to);
    }

    @Inject
    PersistentDataStore persistentDataStore;

    @Inject
    MainThreadBus bus;

    @Bind(R.id.menu_items_view)
    ListView drawerListView;

    private DrawerLayout draverLayout;

    private NavigationDrawerCallbacks callback;
    private ActionBarDrawerToggle drawerToggle;

    private int currentSelectedPosition = 0;
    private View.OnClickListener originalToolbarListener;
    private MainMenuAdapter adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }


    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        getDaggerComponent().inject(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fr_main_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareMenuList();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (currentSelectedPosition != position) {
            drawerListView.setItemChecked(position, true);
            final int previousPosition = currentSelectedPosition;
            currentSelectedPosition = position;
            callback.onNavigationDrawerItemSelectionChanged(previousPosition, position);
            draverLayout.closeDrawers();
        }

        if(position == 1) {
            adapter.setMessagesCount(0);
        }
    }

    @Subscribe
    public void onNewMessageReceived(MessageReceivedEvent event) {
        if(adapter != null) {
            adapter.setMessagesCount(persistentDataStore.getUnreadMsgCount());
        }
    }

    public ActionBarDrawerToggle getDrawerTogle() {
        return drawerToggle;
    }

    public View.OnClickListener getOriginalToolbarListener() {
        return originalToolbarListener;
    }

    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar) {
        this.draverLayout = drawerLayout;
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu();
            }
        };
        originalToolbarListener = drawerToggle.getToolbarNavigationClickListener();
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void prepareMenuList() {
        adapter = new MainMenuAdapter(getActivity(), getMenuItems());
        adapter.setMessagesCount(persistentDataStore.getUnreadMsgCount());
        drawerListView.setAdapter(adapter);
        drawerListView.setOnItemClickListener(this);
        drawerListView.setItemChecked(currentSelectedPosition, true);
    }


    private List<String> getMenuItems() {
        return Arrays.asList(getResources().getStringArray(R.array.menu));
    }

}
