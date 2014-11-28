package iiitd.mc.timetracker.context;

import java.util.List;

import iiitd.mc.timetracker.data.Task;
import iiitd.mc.timetracker.helper.DatabaseController;
import iiitd.mc.timetracker.helper.DatabaseHelper;

/**
 * Extends the basic Task model with a probability field for fuzzy recommendations.
 * @author sebastian
 *
 */
public class SuggestedTask implements Comparable<SuggestedTask>
{
	private Task task;
	private double probability = 0.5;
	DatabaseController dc_obj = new DatabaseController(null);
	
	
	public SuggestedTask(Task task, double probability)
	{
		setTask(task);
		setProbability(probability);
	}
	
	
	public double getProbability()
	{
		return probability;
	}
	
	
	/**
	 * Set the probability for this suggested task.
	 * @param probability Must be between 0 and 1, values out of this range will be set to 0 or 1 accordingly.
	 */
	public void setProbability(double probability)
	{
		if(probability < 0)
			this.probability = 0;
		else if(probability > 1)
			this.probability = 1;
		else
			this.probability = probability;
	}
	/**
	 * Add the given probability.
	 * @param additionalProbability Probability to be added. Must be between 0 and 1, values out of this range will be set to 0 or 1 accordingly.
	 */
	public void increaseProbability(double additionalProbability)
	{
		setProbability(this.probability + additionalProbability);
	}


	public Task getTask()
	{
		
		return task;
	}


	public void setTask(Task task)
	{
		this.task = task;
	}
	
	
	@Override
	public String toString()
	{
		return task.toString();
	}
	
	/**
	 * Checks whether the wrapped Tasks (!) are equal.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof SuggestedTask)) {
            return false;
        }
		
		return ((SuggestedTask)o).getTask().equals(this.task);
	}


	@Override
	public int compareTo(SuggestedTask another)
	{
		int r = Double.compare(this.getProbability(), ((SuggestedTask)another).getProbability());
		return -r;
	}
}
