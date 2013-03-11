package edu.samplu.admin.test;

import org.junit.Test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class ConfigNameSpaceBlanketAppWDIT extends WebDriverLegacyITBase {

    public static final String TEST_URL=ITUtil.PORTAL+"?channelTitle=Namespace&channelUrl="+ITUtil.getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation="+ITUtil.PORTAL_URL+"&hideReturnLink=true";
    
        
    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.WebDriverLegacyITBase#getTestUrl()
     */
    @Override
    public String getTestUrl() {
        // TODO dmoteria - THIS METHOD NEEDS JAVADOCS
        return TEST_URL;
    }
    
    @Test
    public void test() throws Exception
    {
        System.out.println("This is base url =================================: "+ITUtil.getBaseUrlString());
        super.testConfigNamespaceBlanketApprove();
    }
    
}
