package org.kuali.rice.krad.datadictionary;

import org.kuali.rice.krad.uif.view.View;

import java.util.Stack;

/**
 * Holds preloaded view instances up to a configured size
 * 
 * <p>
 * The initial creation of the view object from Spring can be expensive in certain cases. To help with this, views
 * can be preloaded with this pool class. When a request for a new view instance is made, a check will be done first
 * to see if there is a pool and if so pull the already loaded view
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see UifDictionaryIndex#getViewById(java.lang.String)
 */
public class UifViewPool {
    private int maxSize;
    private Stack<View> views;

    public UifViewPool() {
        maxSize = 1;
        views = new Stack<View>();
    }

    public UifViewPool(int maxSize) {
        this.maxSize = maxSize;
        views = new Stack<View>();
    }

    /**
     * Maximum number of view instances the pool can hold
     *
     * <p>
     * On initial startup of the application (during dictionary loading), view instances will be loaded and
     * filled in a pool up to the max size configuration. The default is to preload one view, and each time
     * the view is retrieved it is replaced. If a request is made before the view is replaced, the view is rebuilt
     * from Spring. Therefore the performance gain is not present. For views with high concurrency, this property
     * can be tweaked as needed. Please note larger pool sizes cost more in memory storage and application
     * start up time
     * </p>
     *
     * @return int max pool size
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Setter for the pool max size
     *
     * @param maxSize
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Adds a view instance to the pool
     *
     * @param view - view instance to add
     */
    public void addViewInstance(View view) {
        views.push(view);
    }

    /**
     * Retrieves a view instance from the pool
     *
     * @return View instance
     */
    public View getViewInstance() {
        return views.pop();
    }

    /**
     * Indicates whether the pool is full (number of view instances equals configured max size)
     *
     * @return boolean true if pool is full, else if not
     */
    public boolean isFull() {
        return views.size() == maxSize;
    }

    /**
     * Indicates whether the pool is empty (contains no view instances)
     *
     * <p>
     * When the pool is empty, no view instances may be retrieved until the pool requires view instances.
     * The calling code may choose to wait for a period of time and check again, or request a view instance from
     * Spring
     * </p>
     *
     * @return boolean true if the pool is empty, false if not
     */
    public boolean isEmpty() {
        return (views == null) || (views.size() == 0);
    }
}
