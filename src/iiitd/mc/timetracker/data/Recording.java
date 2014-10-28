package iiitd.mc.timetracker.data;

import iiitd.mc.timetracker.ApplicationHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;

/**
 * A Recording describes a specific time period spent working on a Task.
 * @author gullal
 *
 */
public class Recording 
{
	/**
	 * @author Shubham
	 * Member for Id variable added
	 */
	private long recordingId;
	private Date start;
	private Date end;
	private Task task;
	
	
	/**
	 * Construct a new Recording instance.
	 * @param recordingId The unique ID to identify the recording.
	 * @param task The task being recorded for that time.
	 * @param start The time when started recording.
	 * @param end The time when finished recording.
	 */
	public Recording(long recordingId, Task task, Date start, Date end)
	{
		setRecordingId(recordingId); //TODO: ID assigned by DatabaseController only?
		setTask(task);
		setStart(start);
		setEnd(end);
	}
	public Recording()
	{
		
	}
	
	
	/**
	 * Get the recordingId for the recording performed.
	 * @return the recordingId
	 */
	public long getRecordingId() {
		return recordingId;
	}
	/**
	 * Set the recordingId for the recording performed
	 * @param recordingId the recordingId to set
	 */
	public void setRecordingId(long recordingId) {
		this.recordingId = recordingId;
	}
	/**
	 * Get time when work on the recording was started.
	 * @return 
	 */
	public Date getStart()
	{
		return start;
	}
	/**
	 * Set time when work on the recording was started.
	 * @param start The new start time.
	 */
	public void setStart(Date start)
	{
		this.start = start;
	}
	/**
	 * Get time when work on the recording was stopped.
	 * @return
	 */
	public Date getEnd()
	{
		return end;
	}
	/**
	 * Set time when work on the recording was stopped.
	 * @param end
	 */
	public void setEnd(Date end)
	{
		this.end = end;
	}
	/**
	 * Get the task for which work was done during this recording.
	 * @return
	 */
	public Task getTask()
	{
		return task;
	}
	/**
	 * Set the task for which work was done during this recording.
	 * @param task
	 */
	public void setTask(Task task)
	{
		this.task = task;
	}
	
	/**
	 * Get the Duration of this Recording in the desired unit.
	 * from: http://stackoverflow.com/questions/1555262/calculating-the-difference-between-two-java-date-instances
	 * @param timeUnit The time unit in which the duration should be returned.
	 * @return The duration of this Recording. If the Recording is still running, the current duration.
	 */
	public long getDuration(TimeUnit timeUnit)
	{
		if(start == null)
			return 0;
		
		// set end to NOW if not set already
		Date tEnd = end;
		if(tEnd == null)
			tEnd = new Date();
		
	    long diffInMillies = tEnd.getTime() - start.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	
	@Override
	public String toString()
	{
		DateFormat dateFormatter = android.text.format.DateFormat.getDateFormat(ApplicationHelper.getAppContext());
		DateFormat timeFormatter = new SimpleDateFormat("HH:mm");
		String sTime = "?";
		if(start != null)
			sTime = dateFormatter.format(start) + " " + timeFormatter.format(start);
		String eTime = "?";
		if(end != null)
			eTime = dateFormatter.format(end) + " " + timeFormatter.format(end);
		return getTask().toString() + " [" + sTime + " - " + eTime + "]";
	}
}
