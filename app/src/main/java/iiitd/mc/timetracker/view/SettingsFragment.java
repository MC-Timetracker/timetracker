package iiitd.mc.timetracker.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.database.DatabaseExportImport;

public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_PREF_NOTIFY = "pref_notify";
    private static final int REQUEST_CSV_IMPORT = 1;


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
                        writeExportFile();
                        return true;
                    }
                });
    }

    private void writeExportFile() {
        List<String> dataLines = DatabaseExportImport.exportCsv();

        String filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"
                + DatabaseExportImport.getDefaultExportFileName();

        try {
            BufferedWriter exportWriter = new BufferedWriter(new FileWriter(filename, false));
            for (String line : dataLines) {
                exportWriter.write(line);
                exportWriter.newLine();
            }

        } catch (IOException e) {
            Toast toast = Toast.makeText(ApplicationHelper.getAppContext(), R.string.msg_export_error_file, Toast.LENGTH_SHORT);
            toast.show();

            Toast toastPath = Toast.makeText(ApplicationHelper.getAppContext(), filename, Toast.LENGTH_LONG);
            toastPath.show();

            return;
        }

        Toast toast = Toast.makeText(ApplicationHelper.getAppContext(), R.string.msg_export_success, Toast.LENGTH_SHORT);
        toast.show();
        Toast toastPath = Toast.makeText(ApplicationHelper.getAppContext(), filename, Toast.LENGTH_LONG);
        toastPath.show();
    }

    private void setupImport() {
        findPreference("import").setOnPreferenceClickListener(
                new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        selectImportFile();
                        return true;
                    }
                });
    }

    private void selectImportFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        if (intent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CSV_IMPORT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CSV_IMPORT && resultCode == Activity.RESULT_OK) {
            askResetAndImport(data.getData());
        }
    }

    private void askResetAndImport(final Uri csvUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        AlertDialog dialog = builder.setMessage(R.string.prompt_reset_database)
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        readImportFile(csvUri, true);
                    }
                })
                .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        readImportFile(csvUri, false);
                    }
                })
                .create();
        dialog.show();
    }

    private void readImportFile(Uri csvUri, boolean resetDatabase) {
        if (resetDatabase) {
            DatabaseExportImport.resetDatabase();
        }

        List<String> csvLines = new ArrayList<>();
        try {
            BufferedReader importReader = new BufferedReader(new FileReader(csvUri.getPath()));

            String line;
            while ((line = importReader.readLine()) != null) {
                csvLines.add(line);
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(ApplicationHelper.getAppContext(), R.string.msg_import_error_file, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        DatabaseExportImport.importCsv(csvLines);

        Toast toast = Toast.makeText(ApplicationHelper.getAppContext(), R.string.msg_import_success, Toast.LENGTH_SHORT);
        toast.show();
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