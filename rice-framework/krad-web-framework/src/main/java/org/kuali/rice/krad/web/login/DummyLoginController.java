/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.web.login;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * Basic controller KRAD dummy login.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/login")
public class DummyLoginController extends UifControllerBase {

    @Override
    protected UifFormBase createInitialForm() {
        return new DummyLoginForm();
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=submit")
    public ModelAndView submit(@ModelAttribute("KualiForm") DummyLoginForm uifForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String returnUrl = decode(uifForm.getReturnLocation());
        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.APPLICATION_URL_KEY);
        }

        Properties props = new Properties();
        String user = uifForm.getLogin_user();
        if (StringUtils.isNotBlank(user)) {
            props.put("__login_user", user);
        }

        String password = uifForm.getLogin_pw();
        if (StringUtils.isNotBlank(password)) {
            props.put("__login_pw", password);
        }

        return performRedirect(uifForm, returnUrl, props);
    }

    /**
     * Method to logout the backdoor user and return to the view.
     *
     * @return the view to return to
     */
    @RequestMapping(params = "methodToCall=backdoorLogout")
    public ModelAndView backdoorLogout(@ModelAttribute("KualiForm") DummyLoginForm uifForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String returnUrl = decode(uifForm.getReturnLocation());

        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.APPLICATION_URL_KEY);
        }

        UserSession userSession = KRADUtils.getUserSessionFromRequest(request);
        if (userSession.isBackdoorInUse()) {
            userSession.clearBackdoorUser();
        }

        return performRedirect(uifForm, returnUrl, new Properties());
    }

    @RequestMapping(params = "methodToCall=logout")
    public ModelAndView logout(@ModelAttribute("KualiForm") UifFormBase form, HttpServletRequest request,
            HttpServletResponse response) {
        UserSession userSession = GlobalVariables.getUserSession();

        if (userSession.isBackdoorInUse()) {
            userSession.clearBackdoorUser();
        }

        request.getSession().invalidate();
        return returnToHub(form);
    }

    private String decode(String encodedUrl) {
        try {
            if (StringUtils.isNotBlank(encodedUrl)) {
                return URLDecoder.decode(encodedUrl, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to decode value: " + encodedUrl, e);
        }

        return null;
    }

}
