/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.rice.krms.impl.repository.mock;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krms.api.repository.RuleManagementService;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageUsage;

/**
 *
 * @author nwright
 */
public class KrmsNaturalLanguageUsageLoader {

    private RuleManagementService ruleManagementService = null;

    public RuleManagementService getRuleManagementService() {
        return ruleManagementService;
    }

    public void setRuleManagementService(RuleManagementService ruleManagementService) {
        this.ruleManagementService = ruleManagementService;
    }

    public void loadNlUsage(String id, String name, String nameSpace, String description) {
        NaturalLanguageUsage.Builder bldr = NaturalLanguageUsage.Builder.create(name, nameSpace);
        bldr.setId(id);
        bldr.setActive(true);
        bldr.setDescription(description);
        NaturalLanguageUsage existing = this.findExisting(bldr);
        if (existing == null) {
            this.getRuleManagementService().createNaturalLanguageUsage(bldr.build());
        } else {
            bldr.setVersionNumber(existing.getVersionNumber());
            this.getRuleManagementService().updateNaturalLanguageUsage(bldr.build());
        }
    }

    private NaturalLanguageUsage findExisting(NaturalLanguageUsage.Builder bldr) {
        if (bldr.getId() != null) {
            try {
                return this.getRuleManagementService().getNaturalLanguageUsage(bldr.getId());
            } catch (RiceIllegalArgumentException ex) {
                return null;
            }
        }
        return this.getRuleManagementService().getNaturalLanguageUsageByNameAndNamespace(bldr.getName(), bldr.getNamespace());
    }

    public void load() {
        loadNlUsage("KS-KRMS-NL-USAGE-1000", "kuali.krms.edit", "KS-SYS", "Kuali Rule Edit");
        loadNlUsage("KS-KRMS-NL-USAGE-1001", "kuali.krms.composition", "KS-SYS", "Kuali Rule Composition");
        loadNlUsage("KS-KRMS-NL-USAGE-1002", "kuali.krms.example", "KS-SYS", "Kuali Rule Example");
        loadNlUsage("KS-KRMS-NL-USAGE-1003", "kuali.krms.preview", "KS-SYS", "Kuali Rule Preview");
        loadNlUsage("KS-KRMS-NL-USAGE-1004", "kuali.krms.type.description", "KS-SYS", "Kuali Rule Type Description");
        loadNlUsage("KS-KRMS-NL-USAGE-1005", "kuali.krms.catalog", "KS-SYS", "Kuali Rule Catalog");
        loadNlUsage("KS-KRMS-NL-USAGE-1006", "kuali.krms.type.instruction", "KS-SYS", "Kuali Rule Type Instructions");
    }
}
