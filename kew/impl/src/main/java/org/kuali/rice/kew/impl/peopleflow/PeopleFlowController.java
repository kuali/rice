package org.kuali.rice.kew.impl.peopleflow;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.web.controller.MaintenanceDocumentController;
import org.kuali.rice.krad.web.form.MaintenanceForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for the people flow maintenance document
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/peopleFlowMaintenance")
public class PeopleFlowController extends MaintenanceDocumentController {

    /**
     * Invoked for the refresh of the type attributes group to prepare the type attribute bos for the peopleflow
     */
    @RequestMapping(params = "methodToCall=" + "prepareTypeAttributes")
    public ModelAndView prepareTypeAttributes(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        MaintenanceForm maintenanceForm = (MaintenanceForm) form;

        PeopleFlowBo peopleFlow =
                (PeopleFlowBo) maintenanceForm.getDocument().getNewMaintainableObject().getDataObject();
        if (StringUtils.isNotBlank(peopleFlow.getTypeId())) {
            peopleFlow.rebuildTypeAttributes();
        }

        return super.updateComponent(form, result, request, response);
    }
}
