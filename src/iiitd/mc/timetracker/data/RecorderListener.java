package iiitd.mc.timetracker.data;

import java.util.EventListener;

public interface RecorderListener extends EventListener
{
	/**
	 * Called whenever the recorder changes its state.
	 * e.g. stopped/started recording a task.
	 * @param e
	 */
	public void onRecorderStateChanged(RecorderEvent e);
}
