package org.kuali.rice.core.impl.component;

/**
 * A Data Access Object which handles data operations related to the tracking of component sets
 * which have been published to the component system.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ComponentSetDao {

    ComponentSetBo getComponentSet(String componentSetId);

    /**
     * Saves the given ComponentSetBo, in the case that an optimistic locking exception occurs, it "eats" the exception
     * and returns "false".  Otherwise, if the save is successful it returns "true".
     */
    boolean saveIgnoreLockingFailure(ComponentSetBo componentSetBo);

}
