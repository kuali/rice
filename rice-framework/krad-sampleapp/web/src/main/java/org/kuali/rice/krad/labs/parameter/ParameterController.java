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
package org.kuali.rice.krad.labs.parameter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.ParameterKey;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the Parameter utility.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/parameter")
public class ParameterController extends UifControllerBase {

    @Override
    protected ParameterForm createInitialForm() {
        return new ParameterForm();
    }

    @RequestMapping(params = "methodToCall=update")
    public ModelAndView update(@ModelAttribute("KualiForm") UifFormBase form) {
        ParameterForm parameterForm = (ParameterForm) form;

        String applicationId = KRADConstants.DEFAULT_PARAMETER_APPLICATION_ID;
        String namespaceCode = parameterForm.getNamespaceCode();
        String componentCode = parameterForm.getComponentCode();
        String parameterName = parameterForm.getParameterName();

        Parameter parameter = null;

        if (StringUtils.isNotBlank(applicationId) && StringUtils.isNotBlank(namespaceCode)
            && StringUtils.isNotBlank(componentCode) && StringUtils.isNotBlank(parameterName)) {
            ParameterKey key = ParameterKey.create(applicationId, namespaceCode, componentCode, parameterName);
            parameter = CoreServiceApiServiceLocator.getParameterRepositoryService().getParameter(key);
        }

        if (parameter != null) {
            Parameter.Builder builder = Parameter.Builder.create(parameter);
            builder.setValue(parameterForm.getParameterValue());

            CoreServiceApiServiceLocator.getParameterRepositoryService().updateParameter(builder.build());
        }

        return getModelAndView(form);
    }
}
