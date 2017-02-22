package com.betterx.android.ui.fragments;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.betterx.android.R;
import com.betterx.android.asynktasks.GeocodingTask;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.Frequency;
import com.betterx.android.dataModel.UserData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class UserInfoSetupFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, SeekBar.OnSeekBarChangeListener {

    @Inject
    PersistentDataStore persistentDataStore;

    @Bind(R.id.user_info_toolbar)
    Toolbar toolbar;

    @Bind(R.id.user_info_gender)
    RadioGroup gender;
    @Bind(R.id.user_info_age)
    AppCompatSpinner ageSpinner;
    @Bind(R.id.user_info_location)
    EditText locationField;
    @Bind(R.id.user_info_timezone)
    AppCompatSpinner timezoneSpinner;
    @Bind(R.id.user_info_city)
    EditText cityField;
    @Bind(R.id.user_info_country)
    EditText countryField;
    @Bind(R.id.user_info_education)
    AppCompatSpinner educationField;
    @Bind(R.id.user_info_phone_usage)
    SeekBar phoneUsageSeekBar;
    @Bind(R.id.user_info_web_usage)
    SeekBar webUsageSeekBar;

    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_setup_user_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareEducationSpinner();
        prepareTimezoneSpinner();
        prepareAgeSpinner();
        prepareToolbar();

        phoneUsageSeekBar.setOnSeekBarChangeListener(this);
        webUsageSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showAlert(R.string.cant_get_location_msg);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(!isVisible()) {
            return;
        }

        if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
            locationField.setText(location.getLatitude() + "," + location.getLongitude());
            requestAddress(location);
        } else {
            showAlert(R.string.cant_get_location_msg);
        }
        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @OnClick(R.id.user_info_btn_next)
    public void onNextBtnClick(View v) {
        if(ageSpinner.getSelectedItemPosition() == 0) {
            showAlert(getString(R.string.to_young_msg));
            return;
        }

        final UserData userData = getUserData();
        persistentDataStore.saveUserData(userData);
        replaceFragment(new TransmissionSettingsFragment(), true);
    }

    @OnClick(R.id.user_info_location_detect)
    public void onDetectLocation() {
        buildGoogleApiClient();
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

    private void prepareAgeSpinner() {
        final List<String> ageList = getAgeList();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ageList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(adapter);
    }

    private void prepareTimezoneSpinner() {
        final List<String> timezones = Arrays.asList(getResources().getStringArray(R.array.timezones));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, timezones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timezoneSpinner.setAdapter(adapter);
    }

    private void prepareEducationSpinner() {
        final List<String> timezones = Arrays.asList(getResources().getStringArray(R.array.education));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, timezones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationField.setAdapter(adapter);
    }

    private List<String> getAgeList() {
        final List<String> result = new ArrayList<>();
        for(int i = 0; i < 83; i++) {
            if(i == 0) {
                result.add(getString(R.string.younger_18));
            } else if(i == 82) {
                result.add(getString(R.string.older_100));
            } else {
                result.add("" + (i+17));
            }
        }
        return result;
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
    }

    private void requestAddress(Location location) {
        final GeocodingTask task = new GeocodingTask(getActivity()) {
            @Override
            protected void onPostExecute(Address address) {
                if(isVisible()) {
                    countryField.setText(address.getCountryName());
                    cityField.setText(address.getLocality());
                }
            }
        };
        task.execute(location);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        final int position = seekBar.getProgress();
        if(position < 13) {
            seekBar.setProgress(0);
        } else if (position <= 38) {
            seekBar.setProgress(25);
        } else if (position <= 63) {
            seekBar.setProgress(50);
        } else if (position <= 88) {
            seekBar.setProgress(75);
        } else {
            seekBar.setProgress(100);
        }
    }

    private UserData getUserData() {
        final UserData userData = new UserData();
        userData.gender = gender.getCheckedRadioButtonId() == R.id.user_info_gender_male
                ? getString(R.string.male) : getString(R.string.female);
        userData.age = (String) ageSpinner.getSelectedItem();
        userData.city = cityField.getText().toString();
        userData.country = countryField.getText().toString();
        userData.education = (String) educationField.getSelectedItem();
        userData.phoneUseFrequency = getFrequency(phoneUsageSeekBar);
        userData.webOnPhoneUseFrequency = getFrequency(webUsageSeekBar);
        userData.timezone = (String) timezoneSpinner.getSelectedItem();

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

    private Frequency getFrequency(SeekBar seekBar) {
        final int progress = seekBar.getProgress();
        if(progress == 0) {
            return Frequency.RARELY;
        } else if(progress == 25) {
            return Frequency.NOT_OFTEN;
        } else if(progress == 50) {
            return Frequency.OFTEN;
        } else if(progress == 75) {
            return Frequency.VERY_OFTEN;
        } else {
            return Frequency.ADDICTED;
        }
    }

}
