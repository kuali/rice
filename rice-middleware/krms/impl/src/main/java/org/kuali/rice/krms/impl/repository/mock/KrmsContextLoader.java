/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.rice.krms.impl.repository.mock;

import org.kuali.rice.krms.api.repository.RuleManagementService;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;

/**
 *
 * @author nwright
 */
public class KrmsContextLoader {

    private RuleManagementService ruleManagementService = null;

    public RuleManagementService getRuleManagementService() {
        return ruleManagementService;
    }

    public void setRuleManagementService(RuleManagementService ruleManagementService) {
        this.ruleManagementService = ruleManagementService;
    }
    
    public void loadContext(String id, String namespace, String name, String typeId, String description) {
//        CNTXT_ID	NMSPC_CD	NM	TYP_ID	???? What kind of type	ACTV	VER_NBR	DESC_TXT
        ContextDefinition.Builder bldr = ContextDefinition.Builder.create(namespace, name);
        bldr.setId(id);
        bldr.setActive(true);
        bldr.setTypeId(typeId);
        bldr.setDescription(description);
        this.getRuleManagementService().createContext(bldr.build());
    }

    public void load() {
        loadContext("10000", "KS-SYS", "Course Requirements", "T1004", "Course Requirements");
        loadContext("10001", "KS-SYS", "Program Requirements", "T1004", "Program Requirements");
    }

}
