/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.core.config.spring;

import org.kuali.rice.core.config.spring.RiceConfigPropertyPlaceholderConfigurer.PlaceholderResolvingStringValueResolver;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.StringValueResolver;

/**
 * This BeanDefinitionVisitor that will allow for injection of Properties objects created
 * from properties of a like prefix identified by a value of $[my.prefix].
 * 
 * example:
 * foo.prop1=bar
 * foo.prop2=foo
 * fooPropertyObject=$[foo.]
 * 
 * results in
 * fooPropertyObject={prop1=bar; prop2=foo} 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RiceConfigBeanDefinitionVisitor extends BeanDefinitionVisitor {

    PlaceholderResolvingStringValueResolver valueResolver;
    
    public RiceConfigBeanDefinitionVisitor(StringValueResolver valueResolver) {
        super(valueResolver);
        this.valueResolver=(PlaceholderResolvingStringValueResolver) valueResolver;
    }
    

    @Override
    protected Object resolveValue(Object value) {
        value = super.resolveValue(value);
        String strValue = null;
        
        if(value instanceof String){
            strValue=(String)value;
        }else if(value instanceof TypedStringValue){
            strValue=((TypedStringValue)value).getValue();
        }
        
        if(strValue!=null&&strValue.startsWith("$[") && strValue.endsWith("]")){
            value = valueResolver.resolvePropertiesValue(strValue.substring(2, strValue.length()-1));
        }
        
        return value;
    }

}
