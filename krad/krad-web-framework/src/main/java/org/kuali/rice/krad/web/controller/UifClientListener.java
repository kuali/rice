package org.kuali.rice.krad.web.controller;

import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller that receives various ajax requests from the client to manager server side state
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/listener")
public class UifClientListener extends UifControllerBase {

    @Override
    protected UifFormBase createInitialForm(HttpServletRequest request) {
        return new UifFormBase();
    }

    /**
     * Invoked from the client when the user is leaving a view (by the portal tabs or other mechanism) to clear
     * the form from session storage
     *
     * @param formKey - key of form that should be cleared
     * @return String json success string
     */
    @RequestMapping(params = "methodToCall=clearForm")
    public
    @ResponseBody
    String clearForm(@RequestParam("formKey") String formKey, HttpServletRequest request,
            HttpServletResponse response) {

        // clear form from session
        GlobalVariables.getUifFormManager().removeFormWithHistoryFormsByKey(formKey);

        return "{status:success}";
    }

}
