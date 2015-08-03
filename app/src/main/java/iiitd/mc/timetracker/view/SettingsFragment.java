package iiitd.mc.timetracker.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;

public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_PREF_NOTIFY = "pref_notify";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setVersionInfo();
        setAboutInfo();
    }

    private void setAboutInfo() {
        findPreference("about").setOnPreferenceClickListener(
                new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(findPreference("about").getSummary().toString()));
                        startActivity(intent);
                        return true;
                    }
                });
    }

    private void setVersionInfo() {
        Context context = ApplicationHelper.getAppContext();

        try {
            findPreference("version").setSummary(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}