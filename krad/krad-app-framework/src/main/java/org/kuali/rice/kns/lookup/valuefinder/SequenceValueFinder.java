package org.kuali.rice.kns.lookup.valuefinder;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;

public class SequenceValueFinder implements ValueFinder {

    private final Class<? extends BusinessObject> associatedBo;
    private final String sequenceName;

    public SequenceValueFinder(final Class<? extends BusinessObject> associatedBo, final String sequenceName) {
        this.associatedBo = associatedBo;
        this.sequenceName = sequenceName;
    }

    /**
     * Get the next sequence number value as a Long.
     */
    public final Long getLongValue() {
    	SequenceAccessorService sas = KNSServiceLocator.getSequenceAccessorService();
        return sas.getNextAvailableSequenceNumber(sequenceName, associatedBo);
    }

    /**
     * @see org.kuali.rice.kns.lookup.valuefinder.ValueFinder#getValue()
     */
    public final String getValue() {
        return getLongValue().toString();
    }

	public final String getSequenceName() {
		return sequenceName;
	}

    public final Class<? extends BusinessObject> getAssociatedBo() {
        return associatedBo;
    }
}
