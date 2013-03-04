package edu.samplu.krad.travelview;

import edu.samplu.common.MenuNavITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class KradMenuNavITBase extends MenuNavITBase {
    @Override
    protected String getCreateNewLinkLocator() {
        return "Create New";
    }

    @Override
    protected String getMenuLinkLocator() {
        return "KRAD";
    }
}
