package iiitd.mc.timetracker.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import iiitd.mc.timetracker.ApplicationHelper;

/**
 * A Recording describes a specific time period spent working on a Task.
 */
public class Recording implements Comparable<Recording> {

    private long recordingId;
    private Date start;
    private Date end;
    private Task task;
    private String macAddress = "";


    /**
     * Construct a new Recording instance.
     *
     * @param recordingId The unique ID to identify the recording.
     * @param task        The task being recorded for that time.
     * @param start       The time when started recording.
     * @param end         The time when finished recording.
     */
    public Recording(long recordingId, Task task, Date start, Date end, String macAddress) {
        setRecordingId(recordingId); //TODO: ID assigned by DatabaseController only?
        setTask(task);
        setStart(start);
        setEnd(end);
        setMacAddress(macAddress);
    }

    public Recording() {

    }


    /**
     * Get the recordingId for the recording performed.
     *
     * @return the recordingId
     */
    public long getRecordingId() {
        return recordingId;
    }

    /**
     * Set the recordingId for the recording performed
     *
     * @param recordingId the recordingId to set
     */
    public void setRecordingId(long recordingId) {
        this.recordingId = recordingId;
    }

    /**
     * Get time when work on the recording was started.
     *
     * @return The start time for this recording.
     */
    public Date getStart() {
        return start;
    }

    /**
     * Set time when work on the recording was started.
     * If the given time is after the end time of this recording, it is not saved.
     *
     * @param start The new start time. If null, time will not be saved.
     */
    public void setStart(Date start) {
        if (start == null)
            return;

        if (end == null || start.before(end))
            this.start = start;
    }

    /**
     * Get time when work on the recording was stopped.
     *
     * @return The end time for this recording or the current time if there is no end set.
     */
    public Date getEnd() {
        if (end != null) {
            return end;
        } else {
            return new Date();
        }
    }

    /**
     * Set time when work on the recording was stopped.
     * If the given time is before the start time of this recording, it is not saved.
     *
     * @param end The new end time. If null, time will not be saved.
     */
    public void setEnd(Date end) {
        if (end == null)
            return;

        if (start == null || end.after(start))
            this.end = end;
    }

    /**
     * Get the task for which work was done during this recording.
     *
     * @return The task instance of this recording.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Set the task for which work was done during this recording.
     *
     * @param task The task to be associated with this recording.
     */
    public void setTask(Task task) {
        this.task = task;
    }

    /**
     * @return the macAddress
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * @param macAddress the macAddress to set
     */
    public void setMacAddress(String macAddress) {
        if (macAddress == null)
            this.macAddress = "";
        else
            this.macAddress = macAddress;
    }

    /**
     * Get the Duration of this Recording in the desired unit.
     * from: http://stackoverflow.com/questions/1555262/calculating-the-difference-between-two-java-date-instances
     *
     * @param timeUnit The time unit in which the duration should be returned.
     * @return The duration of this Recording. If the Recording is still running, the current duration.
     */
    public long getDuration(TimeUnit timeUnit) {
        if (start == null)
            return 0;
        // set end to NOW if not set already
        Date tEnd = end;
        if (tEnd == null)
            tEnd = new Date();

        long diffInMillies = tEnd.getTime() - start.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }


    @Override
    public String toString() {
        DateFormat dateFormatter = android.text.format.DateFormat.getDateFormat(ApplicationHelper.getAppContext());
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        String sTime = "?";
        if (start != null)
            sTime = dateFormatter.format(start) + " " + timeFormatter.format(start);
        String eTime = "?";
        if (end != null)
            eTime = dateFormatter.format(end) + " " + timeFormatter.format(end);
        return getTask().toString() + " [" + sTime + " - " + eTime + "]";
    }

    @Override
    public int compareTo(Recording recording) {
        return recording.getStart().compareTo(this.getStart());
    }
}
