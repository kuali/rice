/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.labs.ruleevent;

import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentController;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/ruleEvent")
public class RuleEventController extends MaintenanceDocumentController {

    @Override
    @RequestMapping(params = "methodToCall=save")
    public ModelAndView save(DocumentFormBase form) {
        MaintenanceDocumentForm docForm = (MaintenanceDocumentForm) form;
        MaintenanceDocument document = docForm.getDocument();

        RuleEventImpl event = new RuleEventImpl(document);
        event.setName("Lab-RuleEventController");
        event.addFact("RuleEventSave", document.getDocumentDataObject());
        event.setRuleMethodName("processRule");
        super.save(docForm);

        return getModelAndView(form);
    }
}
