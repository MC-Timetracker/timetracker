package iiitd.mc.timetracker.data;

import java.util.EventObject;

/**
 * Describes an Event related to Task recording and its automation.
 * @author sebastian
 *
 */
public class RecorderEvent extends EventObject
{
	private RecorderEventState _state;
	
	public RecorderEvent(Object source, RecorderEventState state)
	{
		super(source);
		
		_state = state;
	}
	
	public RecorderEventState getState()
	{
		return _state;
	}
}
