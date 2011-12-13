   /**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kns.maintenance;


import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.ParameterContract;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.api.parameter.Parameter;

   public class ParameterMaintenableImpl extends KualiMaintainableImpl {
    private static final long serialVersionUID = 4914145799502207182L;

    @Override
    public void saveDataObject() {
            if (super.getDataObject() instanceof ParameterContract) {
                ParameterContract object  = (ParameterContract)getDataObject();
                Parameter param = CoreFrameworkServiceLocator.getParameterService().getParameter(object.getNamespaceCode(),object.getComponentCode(),object.getName()) ;
                Parameter.Builder b = Parameter.Builder.create(param);
                b.setValue(object.getValue());
                CoreFrameworkServiceLocator.getParameterService().updateParameter(b.build()) ;
            } else {
                throw new RuntimeException(
                        "Cannot update object of type: " + this.getDataObjectClass() + " with Parameter service");
            }
        }

}
