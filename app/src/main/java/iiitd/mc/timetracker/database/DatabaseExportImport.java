package iiitd.mc.timetracker.database;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
     * Creates a complete backup of the database in CSV format.
     * @return An list of lines as csv strings.
     */
    public static List<String> exportCsv() {
        List<String> lines = new ArrayList<>();

        IDatabaseController db = ApplicationHelper.getDatabaseController();
        db.open();
        for (Recording recording : db.getRecordings()) {
            lines.add(recording2csv(recording));
        }
        db.close();

        return lines;
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


    /**
     * Writes the Recordings given in CSV format to the database,
     * creating Tasks on the fly if they do now exist yet.
     *
     * @param csvLines A list of Strings, each representing csv data of one recording.
     */
    public static void importCsv(List<String> csvLines) {
        IDatabaseController db = ApplicationHelper.getDatabaseController();
        db.open();
        for (String line : csvLines) {
            Recording recording = csv2recording(line);
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


    /**
     * Clears the database of all existing recordings and tasks.
     * Use with care!
     */
    public static void resetDatabase() {
        IDatabaseController db = ApplicationHelper.getDatabaseController();
        db.open();
        db.resetDatabase();
        db.close();
    }
}
