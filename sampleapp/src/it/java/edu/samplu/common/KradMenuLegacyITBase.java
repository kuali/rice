package edu.samplu.common;

/**
 * @deprecated Use WebDriverITBase for new tests.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class KradMenuLegacyITBase extends MenuLegacyITBase {
    @Override
    protected String getCreateNewLinkLocator() {
        return "a[title=Create New]";
    }

    @Override
    protected String getMenuLinkLocator() {
        return "a[title=KRAD]";
    }
}
