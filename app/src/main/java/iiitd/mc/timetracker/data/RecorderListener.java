package iiitd.mc.timetracker.data;

/**
 * Implement the RecorderListener interface and register the object through TaskRecorderService.addRecorderListener()
 * to be notified about changes of the Recording.
 */
public interface RecorderListener {

    /**
     * Fires when the Recorder stops a Recording.
     * This fires directly before onRecordingStarted if the recorded Task is switched.
     *
     * @param oldRec The Recording that has been recorded until now.
     */
    public void onRecordingStopped(Recording oldRec);

    /**
     * Fires when the Recorder starts a Recording.
     * This fires also after onRecordingStopped if the recorded Task is switched.
     *
     * @param newRec The new Recording that has just been started.
     */
    public void onRecordingStarted(Recording newRec);
}