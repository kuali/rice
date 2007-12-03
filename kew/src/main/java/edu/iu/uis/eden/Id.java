package edu.iu.uis.eden;

import java.io.Serializable;

/**
 * Superinterface of UserId and GroupId
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface Id extends Serializable {
    /**
     * Returns true if this Id has an empty value. Empty Ids can't be used as keys in a Hash,
     * among other things.
     *
     * @return true if this instance doesn't have a value
     */
    public boolean isEmpty();
}