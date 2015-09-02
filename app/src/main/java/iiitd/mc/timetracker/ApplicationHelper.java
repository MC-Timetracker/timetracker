package iiitd.mc.timetracker;

import android.app.Application;
import android.content.Context;

import iiitd.mc.timetracker.database.CachedDatabaseController;
import iiitd.mc.timetracker.database.IDatabaseController;
import iiitd.mc.timetracker.suggestor.ITaskSuggestor;
import iiitd.mc.timetracker.suggestor.MainTaskSuggestor;

/**
 * Helper class to get the application Context in static functions.
 *
 * @author sebastian
 */
public class ApplicationHelper extends Application {
    private static Context context;
    private static IDatabaseController databaseController;
    private static ITaskSuggestor mainTaskSuggestor;

    public void onCreate() {
        super.onCreate();
        ApplicationHelper.context = getApplicationContext();
    }

    /**
     * Returns the ApplicationContext.
     *
     * @return The app's ApplicationContext.
     */
    public static Context getAppContext() {
        return ApplicationHelper.context;
    }

    /**
     * This DatabaseController factory method creates an instance to use for IDatabaseController.
     * This helps to easily switch to a mockup controller for debugging.
     *
     * @return An instance implementing the IDatabaseController interface.
     */
    public static IDatabaseController getDatabaseController() {
        if (databaseController == null) {
            databaseController = new CachedDatabaseController(context);
        }

        return databaseController;
    }

    public static ITaskSuggestor getMainTaskSuggestor() {
        if (mainTaskSuggestor == null) {
            mainTaskSuggestor = new MainTaskSuggestor();
        }

        return mainTaskSuggestor;
    }
}
