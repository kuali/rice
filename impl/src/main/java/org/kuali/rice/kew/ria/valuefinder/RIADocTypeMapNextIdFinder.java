package org.kuali.rice.kew.ria.valuefinder;

/**
 * The next id finder for the RIADocTypeMap.  Uses the SEQ_RIA_DOCTYPE_MAP_ID sequence.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RIADocTypeMapNextIdFinder extends SequenceNextIdFinder {
	public RIADocTypeMapNextIdFinder() {
        super("KREW_RIA_DOCTYPE_MAP_ID_S");
    }
}
