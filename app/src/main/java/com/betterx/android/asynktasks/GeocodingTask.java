package com.betterx.android.asynktasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

/**
 * this class provide ability to get user city and country by user coordinates asynchronously
 */
public class GeocodingTask extends AsyncTask<Location, Void, Address> {

    private final Context context;

    public GeocodingTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected Address doInBackground(Location... locations) {
        final Geocoder geocoder = new Geocoder(context);

        //get user coordinates
        final Location location = locations[0];
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        try {
            //request user address
            final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return addresses != null && addresses.size() > 0 ? addresses.get(0) : null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
