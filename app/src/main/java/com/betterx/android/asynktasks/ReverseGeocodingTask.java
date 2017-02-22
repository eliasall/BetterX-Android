package com.betterx.android.asynktasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

/**
 * this class provide ability to get user coordinates by user address asynchronously
 */
public class ReverseGeocodingTask extends AsyncTask<String, Void, Location> {

    private final Context context;

    public ReverseGeocodingTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected Location doInBackground(String... addresses) {
        final Geocoder geocoder = new Geocoder(context);
        //get user address
        final String address = addresses[0];

        try {
            //request user coordinates
            final List<Address> currentAddresses = geocoder.getFromLocationName(address, 1);
            if(currentAddresses != null && currentAddresses.size() > 0) {
                //parse user coordinates
                final Address currentAddress = currentAddresses.get(0);
                final Location location = new Location("");
                location.setLatitude(currentAddress.getLatitude());
                location.setLongitude(currentAddress.getLongitude());
                return location;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
