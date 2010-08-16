package org.kuali.rice.kew.ria.valuefinder;

import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * A value finder which returns the next id from a database sequence
 * via the SequenceAccessorService
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SequenceNextIdFinder implements ValueFinder { 
	/**
	 * The database sequence to query
	 */
    protected String sequenceName;
    
    /**
     * Constructor which takes a mandatory sequence name
     * @param sequenceName the database sequence name
     */
    public SequenceNextIdFinder(String sequenceName) {
    	this.sequenceName = sequenceName;
    }
    
    /**
     * Get the next sequence number value as a Long.
     * @return The next sequence number.
     */
    public Long getLongValue() {
        return KNSServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(this.sequenceName);
    }

    /**
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return getLongValue().toString();
    }
}