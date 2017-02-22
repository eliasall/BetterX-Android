package com.betterx.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import com.betterx.android.R;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.UserData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class SetupUserInfoStep1Fragment extends BaseFragment {

    @Inject
    PersistentDataStore persistentDataStore;

    @Bind(R.id.user_info_toolbar)
    Toolbar toolbar;

    @Bind(R.id.user_info_gender)
    RadioGroup gender;
    @Bind(R.id.user_info_age)
    AppCompatSpinner ageSpinner;
    @Bind(R.id.user_info_education)
    AppCompatSpinner educationField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDaggerComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_setup_personal_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareEducationSpinner();
        prepareAgeSpinner();
        prepareToolbar();
    }

    @OnClick(R.id.user_info_btn_next)
    public void onNextBtnClick(View v) {
        if(ageSpinner.getSelectedItemPosition() == 0) {
            showAlert(getString(R.string.to_young_msg));
            return;
        }

        final UserData userData = getUserData();
        persistentDataStore.saveUserData(userData);
        replaceFragment(new SetupUserInfoStep2Fragment(), true);
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
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.v_spinner_item, ageList);
        adapter.setDropDownViewResource(R.layout.v_spinner_dropdown_item);
        ageSpinner.setAdapter(adapter);
    }

    private void prepareEducationSpinner() {
        final List<String> timezones = Arrays.asList(getResources().getStringArray(R.array.education));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.v_spinner_item, timezones);
        adapter.setDropDownViewResource(R.layout.v_spinner_dropdown_item);
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

    private UserData getUserData() {
        final UserData userData = persistentDataStore.getUserData();
        userData.gender = gender.getCheckedRadioButtonId() == R.id.user_info_gender_male
                ? getString(R.string.male) : getString(R.string.female);
        userData.age = (String) ageSpinner.getSelectedItem();
        userData.education = (String) educationField.getSelectedItem();
        return userData;
    }

}
