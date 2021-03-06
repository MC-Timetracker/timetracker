package iiitd.mc.timetracker.model;

import java.util.ArrayList;
import java.util.List;

import iiitd.mc.timetracker.ApplicationHelper;
import iiitd.mc.timetracker.database.IDatabaseController;

/**
 * A Task describes the details of a specific activity.
 * The user can record time spent for a Task through (multiple) Recordings referencing the Task.
 */
public class Task {
    /**
     * String to separate task names in the hierarchy.
     * e.g. "." for "Studies.Maths.Assignment"
     */
    public static final String THS = ".";

    private long id;
    private String name;
    private Task parent;
    //private String geo;			//TODO: proper data type for location?
    private String description;
    private List<Task> subtasks;


    /**
     * Constructs a new Task.
     *
     * @param name   The name describing the task
     * @param parent If parent is null or not being set the task is considered on the top most hierarchy level.
     */
    public Task(String name, Task parent) {
        setName(name);
        setParent(parent);
    }

    public Task(String name) {
        this(name, null);
    }

    public Task() {

    }


    /**
     * Get the id of the task
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the id of the task
     *
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the name describing the Task.
     *
     * @return The name of the task (without parent).
     */
    public String getName() {
        return name;
    }

    /**
     * Get the full name describing the Task including the path of all parent tasks.
     *
     * @return The full name, including parent hierarchy.
     */
    public String getNameFull() {
        String p = "";
        if (parent != null)
            p = parent.toString() + THS;

        return p + this.name;
    }

    /**
     * Set the name describing the Task.
     *
     * @param name The name to be assigned to this task (without parents).
     *             If the string contains the character used for separating
     *             parent hierarchy, only the last part of the string is assigned as name.
     */
    public void setName(String name) {
        if (name.contains(THS)) {
            this.name = name.substring(name.lastIndexOf(THS));
        } else {
            this.name = name;
        }
    }

    /**
     * Get the parent Task of this Task.
     * Through parent relationships a task hierarchy can be built.
     *
     * @return The parent task instance or null if there is no parent.
     */
    public Task getParent() {
        return parent;
    }

    /**
     * Set the parent Task of this Task.
     * Through parent relationships a task hierarchy can be built.
     */
    public void setParent(Task parent) {
        this.parent = parent;
    }

    /**
     * Get the description text giving further details about the Task.
     *
     * @return The textual description of the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description text giving further details about the Task.
     *
     * @param description The text to be assigned as description to the task.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the list of subtasks, Tasks for which this Task serves as parent.
     * This is a convenience function, modifications of the list will not propagate to the database.
     *
     * @return A list of all tasks for which this task is the parent.
     */
    public List<Task> getSubtasks() {
        if (subtasks == null)
            subtasks = new ArrayList<>();

        IDatabaseController db = ApplicationHelper.getDatabaseController();
        db.open();
        List<Task> subtasks = db.getSubTasks(id);
        db.close();

        return subtasks;
    }

    @Override
    public String toString() {
        return getNameFull();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Task) {
            if (id == ((Task) obj).id)
                return true;
        }
        return false;
    }

}
