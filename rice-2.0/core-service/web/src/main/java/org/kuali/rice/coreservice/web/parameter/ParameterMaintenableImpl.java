/**
 * Copyright 2005-2012 The Kuali Foundation
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
   package org.kuali.rice.coreservice.web.parameter;



import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.ParameterType;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.impl.parameter.ParameterBo;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;

   public class ParameterMaintenableImpl extends KualiMaintainableImpl {
    private static final long serialVersionUID = 4914145799502207182L;

    @Override
    public void saveDataObject() {
            if (super.getDataObject() instanceof ParameterBo) {
                ParameterBo object  = (ParameterBo)getDataObject();

                Parameter param = null;
                if(CoreFrameworkServiceLocator.getParameterService().parameterExists(object.getNamespaceCode(), object.getComponentCode(), object.getName())){
                    param = CoreFrameworkServiceLocator.getParameterService().getParameter(object.getNamespaceCode(),object.getComponentCode(),object.getName()) ;
                }else{
                    ParameterType.Builder builder = ParameterType.Builder.create(object.getParameterTypeCode());
                    param =  CoreFrameworkServiceLocator.getParameterService().createParameter(Parameter.Builder.create(object.getApplicationId(),
                              object.getNamespaceCode(),object.getComponentCode(), object.getName(), builder).build());
                }
                Parameter.Builder b = Parameter.Builder.create(param);
                b.setValue(object.getValue());
                b.setDescription(object.getDescription());
                b.setEvaluationOperator(object.getEvaluationOperator());
                b.setVersionNumber(object.getVersionNumber());
                CoreFrameworkServiceLocator.getParameterService().updateParameter(b.build()) ;



            } else {
                throw new RuntimeException(
                        "Cannot update object of type: " + this.getDataObjectClass() + " with Parameter service");
            }
        }

}
