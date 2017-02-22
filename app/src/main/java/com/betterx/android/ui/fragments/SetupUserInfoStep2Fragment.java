package com.betterx.android.ui.fragments;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.betterx.android.R;
import com.betterx.android.asynktasks.GeocodingTask;
import com.betterx.android.asynktasks.ReverseGeocodingTask;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.UserData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class SetupUserInfoStep2Fragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    @Inject
    PersistentDataStore persistentDataStore;

    @Bind(R.id.user_info_toolbar)
    Toolbar toolbar;

    @Bind(R.id.user_info_location)
    EditText locationField;
    @Bind(R.id.user_info_city)
    EditText cityField;
    @Bind(R.id.user_info_country)
    EditText countryField;

    private ProgressDialog progress;

    private GoogleApiClient googleApiClient;
    private boolean ignoreDelay;
    private boolean ignoreResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_setup_location_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareToolbar();
        ignoreDelay = false;
        ignoreResult = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        hideProgress();
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        hideProgress();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        hideProgress();
        showAlert(R.string.cant_get_location_msg);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(!isVisible() || ignoreResult) {
            return;
        }
        ignoreDelay = true;
        if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
            locationField.setText(location.getLatitude() + "," + location.getLongitude());
            requestAddress(location);
        } else {
            showAlert(R.string.cant_get_location_msg);
            hideProgress();
        }
        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @OnClick(R.id.user_info_btn_next)
    public void onNextBtnClick(View v) {
        hideProgress();
        if(TextUtils.isEmpty(cityField.getText().toString()) || TextUtils.isEmpty(countryField.getText().toString())) {
            showAlert(getString(R.string.fill_all_fields_msg));
            return;
        }

        final Location location = getEnteredLocation();
        if(location == null || (location.getLatitude() == 0 && location.getLongitude() == 0)) {
            requestCoordinates();
            return;
        }

        final UserData userData = getUserData();
        persistentDataStore.saveUserData(userData);
        replaceFragment(new SetupUserInfoStep3Fragment(), true);
    }

    @OnClick(R.id.user_info_location_detect)
    public void onDetectLocation() {
        buildGoogleApiClient();
        showProgress();
    }

    private void prepareToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void getLocation(){
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);

        final FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
        fusedLocationProviderApi.requestLocationUpdates(googleApiClient,  locationRequest, this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isAdded() && isVisible() && !ignoreDelay) {
                    hideProgress();
                    ignoreResult = true;
                    showAlert(R.string.cant_get_location_msg);
                }
            }
        }, 10*1000);//10 sec timeout
    }

    private void requestAddress(Location location) {
        final GeocodingTask task = new GeocodingTask(getActivity()) {
            @Override
            protected void onPostExecute(Address address) {
                if(isVisible() && address != null) {
                    countryField.setText(address.getCountryName());
                    cityField.setText(address.getLocality());
                    hideProgress();
                }
            }
        };
        task.execute(location);
    }

    private void requestCoordinates() {
        final String city = cityField.getText().toString();
        final String country = countryField.getText().toString();
        showProgress();

        final ReverseGeocodingTask task = new ReverseGeocodingTask(getActivity()) {
            @Override
            protected void onPostExecute(Location location) {
                super.onPostExecute(location);
                if(isVisible()) {
                    final UserData userData = getUserData();
                    if(location != null) {
                        userData.longitude = location.getLongitude();
                        userData.latitude = location.getLatitude();
                    }

                    persistentDataStore.saveUserData(userData);
                    replaceFragment(new SetupUserInfoStep3Fragment(), true);
                    hideProgress();
                }
            }
        };
        task.execute(String.format("%s, %s", city, country));
    }

    private UserData getUserData() {
        final UserData userData = persistentDataStore.getUserData();
        userData.city = cityField.getText().toString();
        userData.country = countryField.getText().toString();
        userData.timezone = TimeZone.getDefault().getDisplayName();

        final Location location = getEnteredLocation();
        if(location != null) {
            userData.latitude = location.getLatitude();
            userData.longitude = location.getLongitude();
        }
        return userData;
    }

    private Location getEnteredLocation() {
        final String location = locationField.getText().toString();
        if(TextUtils.isEmpty(location)) {
            return null;
        }

        final String[] coords = locationField.getText().toString().split(",");
        if(coords.length != 2) {
            return null;
        }

        try {
            final Location l = new Location("");
            l.setLatitude(Double.parseDouble(coords[0].trim()));
            l.setLongitude(Double.parseDouble(coords[1].trim()));
            return l;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showProgress() {
        if(progress != null) {
            if(progress.isShowing()) {
                return;
            }
        } else {
            progress = new ProgressDialog(getActivity());
            progress.setTitle(getString(R.string.progress_title));
            progress.setMessage(getString(R.string.progress_msg));
            progress.setCancelable(false);
        }
        progress.show();
    }

    private void hideProgress() {
        if(progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

}
