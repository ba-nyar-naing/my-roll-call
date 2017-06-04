package com.banyar.myrollcall_cumdy;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cumdy.banyar.myrollcall_cumdy.R;

import java.util.Collections;

import static com.banyar.myrollcall_cumdy.MainActivity.academicYearList;

/**
 * Created by banyar on 2/4/17.
 */

public class SettingPreferenceActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Setting");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.setting_toolbar_fragment, new SettingPreferenceFragment()).commit();
    }

    public static class SettingPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_preferences);

            if (academicYearList.size() > 0) {
                Collections.reverse(academicYearList);
                CharSequence[] cs = (CharSequence[]) academicYearList.toArray(new CharSequence[academicYearList.size()]);
                final ListPreference listPreference = (ListPreference) findPreference("academicYear");
                listPreference.setEntries(cs);
                listPreference.setEntryValues(cs);
            }
        }
    }
}
