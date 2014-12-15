package com.personal.fragement;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;


import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import com.personal.android.R;
import com.personal.android.PersonalDairyApplication;
import com.personal.android.ui.components.ProfileComponent;
import com.personal.android.util.EventBus;
import com.personal.android.util.JavaConsts;

/**
 * Base Fragement class to register the screen flow using Google Analytics
 * Registering Bus implementation using
 */
public abstract class BaseFragement extends Fragment {
    private final static String TAG = BaseFragement.class.getSimpleName();

    private ProfileComponent profileComponent;
    protected PersonalDairyContainer fragmentContainer;
    Tracker tracker;

    public abstract String getTitle();

    public ProfileComponent getProfileComponent() {
        return profileComponent;
    }

    /**
     * Second Phase of Fragment initialisation. Gets called in {@link #onActivityCreated(android.os.Bundle)}
     * Detects after the view hierarchy has been set up if there is Profile Component in it and initiates its population
     */
    private void checkForProfileComponent(){
        if (getView() != null) {
            profileComponent = (ProfileComponent) getView().findViewById(R.id.profile_component);
            if (profileComponent != null) {
                Log.v(TAG, "Profile Component found");
                Log.v(TAG, "Populating Profile Component with data from static LocalStorageUtil");
                ((RanxActivity) getActivity()).populateProfileComponent(profileComponent);
            }
        } else {
            Log.e(TAG, "View is null in onActivityCreated Phase of Life Cycle!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        tracker = ((PersonalDairyApplication) getActivity().getApplication()).getTracker(JavaConsts.TrackerName.APP_TRACKER);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.v(TAG, "onAttach (" + getClass().getName() + "; " + activity + ")");
        // Save a reference to the containing object
        this.fragmentContainer = (PersonalDairyContainer) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();

        Log.v(TAG, "onStart (" + getClass().getName() + ")");
    }

    public void onResume(){
        super.onResume();
        EventBus.getInstance().register(this);
        Log.v(TAG, "onResume (" + getClass().getName() + ")");
        this.tracker.set(Fields.SCREEN_NAME, getTitle());
        this.tracker.send( MapBuilder.createAppView().build());
        checkForProfileComponent();
        this.fragmentContainer.setTitle(getTitle());
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getInstance().unregister(this);
        Log.v(TAG, "onPause (" + getClass().getName() + ")");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop (" + getClass().getName() + ")");
    }

    @Override
    public void onDestroyView() {
        Log.v(TAG, "onDestroyView (" + getClass().getName() + ")");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy (" + getClass().getName() + ")");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG, "onDetach (" + getClass().getName() + ")");
        // Remove reference to the container activity
        fragmentContainer = null;
    }
}
