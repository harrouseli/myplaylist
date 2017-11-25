package fr.eisti.android.myplaylist.activities;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import fr.eisti.android.myplaylist.preferences.ListSharedPrefs;
import fr.eisti.android.myplaylist.R;

/**
 * Created by Harrous Elias on 07/03/2015
 */
public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(
                ListSharedPrefs.PREFS_NAME);

        addPreferencesFromResource(R.layout.prefs);

        Preference button = (Preference)findPreference("button");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                finish();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
}
