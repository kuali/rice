package org.kuali.rice.kim.api.common.attribute;


public interface KimAttributeDataHistoryContract extends KimAttributeDataContract {
    /**
     * The id of the history object this AttributeData is associated with.  For
     * example:  this could be a history id of a permission, role, group, or
     * responsibility history object.
     *
     * @return the id
     */
    Long getAssignedToHistoryId();

    //Long getHistoryId();
}
