package iiitd.mc.timetracker.database;

import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.R;
import iiitd.mc.timetracker.TaskRecorderService;
import iiitd.mc.timetracker.model.Recording;
import iiitd.mc.timetracker.model.Task;

/**
 * Provides import/export functionality of the database
 */
public class DatabaseExportImport {
    public static String getDefaultExportFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return "timeturner_backup_" + format.format(new Date()) + ".csv";
    }

    private static String CSV_SEPARATOR = ",";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * Writes a complete backup of the database in CSV format to the default backup file.
     */
    public static void exportCsv() {
        String filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"
                + DatabaseExportImport.getDefaultExportFileName();

        try {
            BufferedWriter exportWriter = new BufferedWriter(new FileWriter(filename, false));
            DatabaseExportImport.exportCsv(exportWriter);
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

    /**
     * Writes a complete backup of the database in CSV format into the given file.
     *
     * @param exportWriter The writer to which the export is written, pointing the the desired output stream.
     */
    public static void exportCsv(BufferedWriter exportWriter) throws IOException {
        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();

        for (Recording recording : db.getRecordings()) {
            exportWriter.write(recording2csv(recording));
            exportWriter.newLine();
        }
        exportWriter.flush();

        db.close();
    }

    private static String recording2csv(Recording recording) {
        String task = recording.getTask().getNameFull();
        String start = dateFormat.format(recording.getStart());
        String end = dateFormat.format(recording.getEnd());
        String mac = recording.getMacAddress();
        String taskDescription = recording.getTask().getDescription();
        if (taskDescription == null) {
            taskDescription = "";
        }

        return escapeString(task) + CSV_SEPARATOR +
                start + CSV_SEPARATOR +
                end + CSV_SEPARATOR +
                mac + CSV_SEPARATOR +
                escapeString(taskDescription);
    }

    private static String escapeString(String s) {
        return s.replaceAll(CSV_SEPARATOR, "\\" + CSV_SEPARATOR);
    }

    private static String unescapeString(String s) {
        return s.replaceAll("\\" + CSV_SEPARATOR, CSV_SEPARATOR);
    }


    public static void importCsv(Uri csvUri) {
        askDatabaseReset();

        try {
            BufferedReader importReader = new BufferedReader(new FileReader(csvUri.getPath()));
            DatabaseExportImport.importCsv(importReader);
        } catch (IOException e) {
            Toast toast = Toast.makeText(ApplicationHelper.getAppContext(), R.string.msg_import_error_file, Toast.LENGTH_SHORT);
            toast.show();

            return;
        }

        Toast toast = Toast.makeText(ApplicationHelper.getAppContext(), R.string.msg_import_success, Toast.LENGTH_SHORT);
        toast.show();
    }

    private static void askDatabaseReset() {

    }

    public static void importCsv(BufferedReader importReader) throws IOException {
        IDatabaseController db = ApplicationHelper.createDatabaseController();
        db.open();

        String csvLine;
        while ((csvLine = importReader.readLine()) != null) {
            Recording recording = csv2recording(csvLine);
            if (recording != null) {
                db.insertRecording(recording);
            }
        }

        db.close();
    }

    private static Recording csv2recording(String csvLine) {
        String[] values = csvLine.split(CSV_SEPARATOR);
        String taskName = unescapeString(values[0]);
        String startString = values[1];
        String endString = values[2];
        String mac = values[3];
        String taskDescription = "";
        if (values.length > 4) {
            taskDescription = unescapeString(values[4]);
        }

        Date startDate;
        Date endDate;
        try {
            startDate = dateFormat.parse(startString);
            endDate = dateFormat.parse(endString);
        } catch (ParseException e) {
            Toast toast = Toast.makeText(ApplicationHelper.getAppContext(), R.string.msg_import_error_date, Toast.LENGTH_SHORT);
            toast.show();

            String msg = taskName + ": '" + startString + "' - '" + endString + "'";
            Toast toastDetails = Toast.makeText(ApplicationHelper.getAppContext(), msg, Toast.LENGTH_LONG);
            toastDetails.show();

            return null;
        }

        Task task = TaskRecorderService.createTaskFromString(taskName);
        task.setDescription(taskDescription);

        Recording recording = new Recording();
        recording.setTask(task);
        recording.setMacAddress(mac);
        recording.setStart(startDate);
        recording.setEnd(endDate);

        return recording;
    }
}
