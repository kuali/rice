package org.kuali.rice.krad.lookup.valuefinder;

import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.SequenceAccessorService;

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
    	SequenceAccessorService sas = KRADServiceLocator.getSequenceAccessorService();
        return sas.getNextAvailableSequenceNumber(sequenceName, associatedBo);
    }

    /**
     * @see ValueFinder#getValue()
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
