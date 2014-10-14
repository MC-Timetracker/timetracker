package iiitd.mc.timetracker.data;

import java.util.Date;

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
	private int recordingId;
	private Date start;
	private Date end;
	private Task task;
	
	/**
	 * Get the recordingId for the recording performed.
	 * @return the recordingId
	 */
	public int getRecordingId() {
		return recordingId;
	}
	/**
	 * Set the recordingId for the recording performed
	 * @param recordingId the recordingId to set
	 */
	public void setRecordingId(int recordingId) {
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
}
