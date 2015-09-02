package iiitd.mc.timetracker.suggestor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.database.IDatabaseController;
import iiitd.mc.timetracker.model.Recording;

public class TimeTaskSuggestor implements ITaskSuggestor {
    private static final int INTERVAL = 60; // interval size in minutes for classification based on recording start time
    private static final int BUCKET_COUNT = 24 * 60 * 2 / INTERVAL; // 24*60 (minutes/day) * 2 (overlap of buckets) / interval
    private static final double PROBABILITY = 0.1;
    private static final int CACHE_TIME = 60 * 60 * 1000; // cache the timeBuckets model for 60 minutes

    private long timestamp;
    private List<List<SuggestedTask>> timeBuckets;

    @Override
    public List<SuggestedTask> getSuggestedTasks() {
        /*
         * Prediction is based on 1 hour buckets, overlapping by 0.5 hour each:
		 * 		00:00 - 01:00
		 * 		00:30 - 01:30
		 *  	01:00 - 02:00
		 *  	01:30 - 02:30
		 *  	...
		 * All existing recordings are added to their two relevant buckets.
		 * The recordings in the two buckets matching the current time are used for suggestions.
		 * SuggestedTask.probability is higher for tasks that occur repeatedly in these relevant buckets.
		 */

        Calendar now = new GregorianCalendar();
        if ((now.getTimeInMillis() - timestamp) > CACHE_TIME) {
            updateTimeBuckets();
            timestamp = now.getTimeInMillis();
        }

        List<SuggestedTask> tasks = new ArrayList<>();
        int bucket1 = getPrimaryBucket(now);
        int bucket2 = getSecondaryBucket(bucket1);

        tasks.addAll(timeBuckets.get(bucket1));
        for (SuggestedTask s : timeBuckets.get(bucket2)) {
            MainTaskSuggestor.addSuggestionToList(tasks, s);
        }

        Collections.sort(tasks);
        return tasks;
    }

    private void updateTimeBuckets() {
        IDatabaseController db = ApplicationHelper.getDatabaseController();
        db.open();
        //TODO: use some limit to only get the recordings of the last 30 (?) days?
        List<Recording> recordings = db.getRecordings();
        db.close();

        timeBuckets = new ArrayList<>(BUCKET_COUNT);
        for (int i = 0; i < 48; i++) {
            // init bucket
            timeBuckets.add(i, new ArrayList<SuggestedTask>());
        }

        // add recordings to buckets
        for (Recording rec : recordings) {
            Calendar d = new GregorianCalendar();
            d.setTime(rec.getStart());
            int bucket1 = getPrimaryBucket(d);
            int bucket2 = getSecondaryBucket(bucket1);

            //TODO: only count one instance of the task per day per bucket
            //	(don't increase probability for repeatedly starting and stopping the same task with very short durations)
            SuggestedTask s = new SuggestedTask(rec.getTask(), PROBABILITY);
            MainTaskSuggestor.addSuggestionToList(timeBuckets.get(bucket1), s);
            MainTaskSuggestor.addSuggestionToList(timeBuckets.get(bucket2), s);
        }
    }

    /**
     * Get the primary bucket index based on a point in time.
     *
     * @param time The time to be mapped to a bucket, the date portion is ignored.
     * @return The index of the primary bucket this time maps to.
     */
    private int getPrimaryBucket(Calendar time) {
        int min = time.get(Calendar.MINUTE);
        min += 60 * time.get(Calendar.HOUR_OF_DAY);

        // e.g. 00:27 -> 27/30=0 -> bucket 0 (& bucket -1 -> bucket BUCKET_COUNT-1, see getSecondaryBucket() )
        // e.g. 00:33 -> 33/30=1 -> bucket 1 (& bucket  0)

        return min / (INTERVAL / 2);
    }

    /**
     * Get the second bucket index based on the primary bucket index.
     *
     * @param bucket The index of the "primary" bucket.
     * @return The index of the secondary bucket the item should also map to.
     */
    private int getSecondaryBucket(int bucket) {
        int r = bucket - 1;
        if (r < 0)
            r = BUCKET_COUNT - 1;

        return r;
    }

}
