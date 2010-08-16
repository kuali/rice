package org.kuali.rice.kew.ria.valuefinder;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;

/**
 * Generic value finder for Yes/No values.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class YesNoValueFinder extends KeyValuesBase {
	/*
	 * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
	 */
	public List<KeyLabelPair> getKeyValues() {
		List<KeyLabelPair> activeLabels = new ArrayList<KeyLabelPair>();

		activeLabels.add(new KeyLabelPair("Y", "Yes"));
		activeLabels.add(new KeyLabelPair("N", "No"));

		return activeLabels;
	}
}
