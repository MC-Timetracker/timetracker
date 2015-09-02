package iiitd.mc.timetracker.suggestor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Merges different tasks into one Task list
 */
public class MainTaskSuggestor implements ITaskSuggestor {
    private ITaskSuggestor[] suggestors = new ITaskSuggestor[]{
            new TopHierarchySuggestor(),
            new RecentTaskSuggestor(),
            new TimeTaskSuggestor(),
            new LocationTaskSuggestor(),
    };
    private double[] suggestorWeights = new double[]{
            0,      // top hierarchy
            0.1,    // recent tasks
            0.4,    // time
            0.5,    // location
    };


    @Override
    public List<SuggestedTask> getSuggestedTasks() {
        List<SuggestedTask> tasks = new ArrayList<>();

        for (int i = 0; i < suggestors.length; i++) {
            ITaskSuggestor suggestor = suggestors[i];

            for (SuggestedTask s : suggestor.getSuggestedTasks()) {
                // scale probability according to weight
                double prob = s.getProbability() * suggestorWeights[i];
                s.setProbability(prob);

                addSuggestionToList(tasks, s);
            }
        }

        Collections.sort(tasks);
        return tasks;
    }

    /**
     * Helper function to add a task to the list of suggestions.
     * Avoids adding duplicate tasks and increases the probability of the task instead.
     *
     * @param list          The list of suggestions to be extended.
     * @param suggestedTask The task to be added to the list.
     */
    public static void addSuggestionToList(List<SuggestedTask> list, SuggestedTask suggestedTask) {
        for (SuggestedTask l : list) {
            if (l.equals(suggestedTask)) {
                // don't add duplicate, instead increase probability of suggested task
                l.increaseProbability(suggestedTask.getProbability());
                return;
            }
        }

        // task was not in the list yet
        list.add(suggestedTask);
    }

}
