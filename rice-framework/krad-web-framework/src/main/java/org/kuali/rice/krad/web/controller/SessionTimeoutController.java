package org.kuali.rice.krad.web.controller;

import org.kuali.rice.krad.web.form.SessionTimeoutForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller class for the session timeout view
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/sessionTimout")
public class SessionTimeoutController extends UifControllerBase {

    @Override
    protected SessionTimeoutForm createInitialForm(HttpServletRequest request) {
        return new SessionTimeoutForm();
    }
}
