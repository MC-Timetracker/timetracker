package iiitd.mc.timetracker.suggestor;

import java.util.ArrayList;
import java.util.List;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.database.IDatabaseController;
import iiitd.mc.timetracker.model.Task;

/**
 * Suggests top hierarchy tasks
 *
 * @author gullal
 */
public class TopHierarchySuggestor implements ITaskSuggestor {

    private IDatabaseController db;

    public TopHierarchySuggestor() {
        db = ApplicationHelper.getDatabaseController();
    }

    @Override
    public List<SuggestedTask> getSuggestedTasks() {
        db.open();
        List<Task> topTasks = db.getTasks();
        db.close();
        //TODO: filter to only get top hierarchy tasks?!

        List<SuggestedTask> tasks = new ArrayList<>();
        for (Task t : topTasks) {
            // wrap the Tasks in SuggestTask objects with a constant probability
            //TODO: what probability for TopHierarchy tasks?
            SuggestedTask item = new SuggestedTask(t, 0);
            tasks.add(item);
        }

        return tasks;
    }

}
