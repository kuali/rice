/*
 * Copyright 2004 Jonathan M. Lehr
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * 
 */
 
// begin Kuali Foundation modification
package org.kuali.rice.kns.web.struts.pojo;

// deleted some imports
// end Kuali Foundation modification


import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

/**
 * begin Kuali Foundation modification
 * This class is the POJO Plugin implementation of the PlugIn interface.
 * end Kuali Foundation modification
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
// Kuali Foundation modification: class originally named SL plugin
public class PojoPlugin implements PlugIn {
    static final Logger logger = Logger.getLogger(PojoPlugin.class.getName());

    public PojoPlugin() {
    }

    public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {
        // begin Kuali Foundation modification
        ConvertUtilsBean convUtils = new ConvertUtilsBean();
        PropertyUtilsBean propUtils = new PojoPropertyUtilsBean();
        BeanUtilsBean pojoBeanUtils = new BeanUtilsBean(convUtils, propUtils);

        BeanUtilsBean.setInstance(pojoBeanUtils);
        logger.fine("Initialized BeanUtilsBean with " + pojoBeanUtils);
        // end Kuali Foundation modification
    }

    public void destroy() {
    }
}
