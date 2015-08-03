package iiitd.mc.timetracker;

import android.app.Application;
import android.content.Context;

import iiitd.mc.timetracker.database.DatabaseController;
import iiitd.mc.timetracker.database.IDatabaseController;

/**
 * Helper class to get the application Context in static functions.
 *
 * @author sebastian
 */
public class ApplicationHelper extends Application {
    private static Context context;

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
    public static IDatabaseController createDatabaseController() {
        //return new MockupDatabaseController();
        return new DatabaseController(ApplicationHelper.context);
    }
}
