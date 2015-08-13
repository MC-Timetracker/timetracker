package iiitd.mc.timetracker.view;

import android.app.Activity;
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
import iiitd.mc.timetracker.database.DatabaseExportImport;

public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_PREF_NOTIFY = "pref_notify";
    private static final int REQUEST_CSV_GET = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setupVersionInfo();
        setupAboutInfo();

        setupExport();
        setupImport();
    }

    private void setupExport() {
        findPreference("export").setOnPreferenceClickListener(
                new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        DatabaseExportImport.exportCsv();
                        return true;
                    }
                });
    }

    private void setupImport() {
        findPreference("import").setOnPreferenceClickListener(
                new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        selectImportCsvFile();
                        return true;
                    }
                });
    }

    private void selectImportCsvFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        if (intent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CSV_GET);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CSV_GET && resultCode == Activity.RESULT_OK) {
            DatabaseExportImport.importCsv(data.getData());
        }
    }


    private void setupAboutInfo() {
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

    private void setupVersionInfo() {
        Context context = ApplicationHelper.getAppContext();

        try {
            findPreference("version").setSummary(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}