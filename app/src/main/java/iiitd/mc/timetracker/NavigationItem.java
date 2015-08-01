package iiitd.mc.timetracker;

/**
 * A NavigationItem is representing the details of an entry in the NavigationDrawer.
 */
public class NavigationItem {
    /**
     * @param stringTitle  The id of the string resource of the text of the item.
     * @param drawableIcon The id of the drawable resource of icon of the item.
     * @param fragment     The Fragment to be loaded upon selecting the item.
     */
    public NavigationItem(int stringTitle, int drawableIcon, Class fragment) {
        this.stringTitle = stringTitle;
        this.drawableIcon = drawableIcon;
        this.fragment = fragment;
    }

    /**
     * The id of the string resource of the text of the item.
     */
    public int stringTitle;

    /**
     * The id of the drawable resource of icon of the item.
     */
    public int drawableIcon;

    /**
     * The Fragment to be loaded upon selecting the item.
     */
    public Class fragment;
}