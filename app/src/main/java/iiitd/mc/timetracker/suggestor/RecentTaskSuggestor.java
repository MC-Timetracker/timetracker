package iiitd.mc.timetracker.suggestor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.database.IDatabaseController;
import iiitd.mc.timetracker.model.Recording;

/**
 * Suggests tasks that were entered by the user previously.
 *
 * @author sebastian & gullal
 */
public class RecentTaskSuggestor implements ITaskSuggestor {

    private IDatabaseController db;
    private static final int RECORDING_LIMIT = 5;

    public RecentTaskSuggestor() {
        db = ApplicationHelper.getDatabaseController();
    }

    @Override
    public List<SuggestedTask> getSuggestedTasks() {
        db.open();
        //TODO: use some limit to only get the last 10 (?) recordings?
        List<Recording> recordings = db.getRecordings();
        db.close();

        List<SuggestedTask> tasks = new ArrayList<>();

        Collections.sort(recordings);

        // add unique tasks to list and update their probability (ratio of occurrences)
        /*
      How many recordings are considered.
     */
        int i = RECORDING_LIMIT;
        double prob = 1.0 / RECORDING_LIMIT;
        for (Recording rec : recordings) {
            if (i <= 0)
                break;

            SuggestedTask temp = new SuggestedTask(rec.getTask(), prob);
            int index = tasks.indexOf(temp);
            if (index == -1) {
                // add new task
                tasks.add(temp);
            } else {
                // add probability to existing task
                tasks.get(index).increaseProbability(prob);
            }

            i--;
        }

        Collections.sort(tasks);
        return tasks;
    }
}
