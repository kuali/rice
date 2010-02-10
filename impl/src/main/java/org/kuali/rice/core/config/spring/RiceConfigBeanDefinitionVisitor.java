package org.kuali.rice.core.config.spring;

import java.util.Properties;

import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.TypedStringValue;

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

    
//------------------------------------------------------------------------------------
// begin spring 2.0 compatible impl
//------------------------------------------------------------------------------------
    @Override
    protected String resolveStringValue(String strVal) {
        return null;
    }

    protected Properties resolvePropertiesValue(String strVal) {
        return null;
    }

    @Override
    protected Object resolveValue(Object value) {
        value = super.resolveValue(value);
        String strValue = null;

        if (value instanceof String) {
            strValue = (String) value;
        } else if (value instanceof TypedStringValue) {
            strValue = ((TypedStringValue) value).getValue();
        }

        if (strValue != null && strValue.startsWith("$[") && strValue.endsWith("]")) {
            value = resolvePropertiesValue(strValue.substring(2, strValue.length() - 1));
        }

        return value;
    }
//------------------------------------------------------------------------------------
// end spring 2.0 compatible impl
//------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------
// begin spring 2.5 compatible impl
//------------------------------------------------------------------------------------
//
//    PlaceholderResolvingStringValueResolver valueResolver;
//    
//    public RiceConfigBeanDefinitionVisitor(StringValueResolver valueResolver) {
//        super(valueResolver);
//        this.valueResolver=(PlaceholderResolvingStringValueResolver) valueResolver;
//    }
//    
//
//    @Override
//    protected Object resolveValue(Object value) {
//        value = super.resolveValue(value);
//        String strValue = null;
//        
//        if(value instanceof String){
//            strValue=(String)value;
//        }else if(value instanceof TypedStringValue){
//            strValue=((TypedStringValue)value).getValue();
//        }
//        
//        if(strValue!=null&&strValue.startsWith("$[") && strValue.endsWith("]")){
//            value = valueResolver.resolvePropertiesValue(strValue.substring(2, strValue.length()-1));
//        }
//        
//        return value;
//    }
//------------------------------------------------------------------------------------
// end spring 2.5 compatible impl
//------------------------------------------------------------------------------------
    
}
