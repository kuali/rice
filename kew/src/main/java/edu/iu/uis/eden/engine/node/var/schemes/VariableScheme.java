/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.iu.uis.eden.engine.node.var.schemes;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.BranchService;
import edu.iu.uis.eden.engine.node.BranchState;
import edu.iu.uis.eden.engine.node.var.Property;
import edu.iu.uis.eden.engine.node.var.PropertyScheme;

/**
 * A property scheme that looks the property up in the state variable map
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public final class VariableScheme implements PropertyScheme {
    private static final Logger LOG = Logger.getLogger(VariableScheme.class);

    public String getName() {
        return "variable";
    }
    public String getShortName() {
        return "var";
    }

    public Object load(Property property, RouteContext context) {
//        try {
//            return PropertyUtils.getProperty(doc.getVariables(), property.locator);
            LOG.debug("getting variable: " + property.locator);
            BranchService branchService = KEWServiceLocator.getBranchService();
            String value = branchService.getScopedVariableValue(context.getNodeInstance().getBranch(), BranchState.VARIABLE_PREFIX + property.locator);
            LOG.debug("variable '" + property.locator + "': " + value);
            return value;

//        } catch (NoSuchMethodException nsme) {
//            throw new RuntimeException("Error loading resource: " + property.locator, nsme);
//        } catch (InvocationTargetException ite) {
//            throw new RuntimeException("Error loading resource: " + property.locator, ite);
//        } catch (IllegalAccessException iae) {
//            throw new RuntimeException("Error loading resource: " + property.locator, iae);
//        }
    }

    public String toString() {
        return "[VariableScheme]";
    }
}