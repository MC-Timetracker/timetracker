package iiitd.mc.timetracker.suggestor;

import java.util.List;

/**
 * Interface for any class implementing filtering or ranking of tasks.
 * The list of tasks processed in this way can then be suggested to the user
 * or used in further TaskSuggesters to combine results.
 *
 * @author sebastian
 */
public interface ITaskSuggestor {
    /**
     * Get an array of the suggested tasks.
     *
     * @return a list of suggested tasks, specifying task and probability.
     */

    List<SuggestedTask> getSuggestedTasks();

}
