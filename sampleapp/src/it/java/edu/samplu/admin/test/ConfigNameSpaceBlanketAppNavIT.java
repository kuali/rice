package edu.samplu.admin.test;

import org.junit.Test;

import edu.samplu.common.ITUtil;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class ConfigNameSpaceBlanketAppNavIT extends AdminMenuBlanketAppNavITBase {

    @Override
    protected String getLinkLocator() {
        return "Namespace";
    }

    @Test
    public void test() throws Exception {
        gotoMenuLinkLocator();
        super.testConfigNamespaceBlanketApprove();
   }
   
}
