package iiitd.mc.timetracker.context;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.data.Recording;
import iiitd.mc.timetracker.helper.IDatabaseController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class TimeTaskSuggestor implements ITaskSuggestor
{
	private final int INTERVAL = 60; // interval size in minutes for classification based on recording start time
	private final int BUCKET_COUNT = 24*60 * 2 / INTERVAL; // 24*60 (minutes/day) * 2 (overlap of buckets) / interval
	private final double PROBABILITY = 0.1; // if a task occurs 5 times in the time period it will have full probability (recordings counted twice if within INTERVAL/2 of point in time, once if within INTERVAL only
	
	@Override
	public List<SuggestedTask> getSuggestedTasks()
	{
		IDatabaseController db = ApplicationHelper.createDatabaseController();
		db.open();
		//TODO: use some limit to only get the recordings of the last 30 (?) days?
		List<Recording> recordings = db.getRecordings();
		db.close();
		
		
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
		
		//TODO: store this "model" and don't count buckets every time anew
		List<List<SuggestedTask>> times = new ArrayList<List<SuggestedTask>>(BUCKET_COUNT);
		for(int i = 0; i < 48; i++)
		{
			// init bucket
			times.add(i, new ArrayList<SuggestedTask>());
		}
		
		// add recordings to buckets
		for(Recording rec : recordings)
		{
			Calendar d = new GregorianCalendar();
			d.setTime(rec.getStart());
			int bucket1 = getPrimaryBucket(d);
			int bucket2 = getSecondaryBucket(bucket1);
			
			//TODO: only count one instance of the task per day per bucket
			//	(don't increase probability for repeatedly starting and stopping the same task with very short durations)
			SuggestedTask s = new SuggestedTask(rec.getTask(), PROBABILITY);
			MainTaskSuggestor.addSuggestionToList(times.get(bucket1), s);
			MainTaskSuggestor.addSuggestionToList(times.get(bucket2), s);
		}
		
		
		List<SuggestedTask> tasks = new ArrayList<SuggestedTask>();
		Calendar now = new GregorianCalendar();
		int bucket1 = getPrimaryBucket(now);
		int bucket2 = getSecondaryBucket(bucket1);
		
		tasks.addAll(times.get(bucket1));
		for(SuggestedTask s : times.get(bucket2))
		{
			MainTaskSuggestor.addSuggestionToList(tasks, s);
		}
		
		Collections.sort(tasks);
		return tasks;
	}
	
	/**
	 * Get the primary bucket index based on a point in time.
	 * @param time The time to be mapped to a bucket, the date portion is ignored.
	 * @return The index of the primary bucket this time maps to.
	 */
	private int getPrimaryBucket(Calendar time)
	{
		int min = time.get(Calendar.MINUTE);
		min += 60 * time.get(Calendar.HOUR_OF_DAY);
		
		int bucket = min / (INTERVAL/2);
		// e.g. 00:27 -> 27/30=0 -> bucket 0 (& bucket -1 -> bucket BUCKET_COUNT-1, see getSecondaryBucket() )
		// e.g. 00:33 -> 33/30=1 -> bucket 1 (& bucket  0)
		
		return bucket;
	}
	
	/**
	 * Get the second bucket index based on the primary bucket index.
	 * @param bucket The index of the "primary" bucket.
	 * @return The index of the secondary bucket the item should also map to.
	 */
	private int getSecondaryBucket(int bucket)
	{
		int r = bucket - 1;
		if(r < 0)
			r = BUCKET_COUNT - 1;
		
		return r;
	}

}
