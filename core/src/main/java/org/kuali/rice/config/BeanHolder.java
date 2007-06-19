package org.kuali.rice.config;

import org.springframework.beans.factory.FactoryBean;

/**
 * The entire purpose of this class is to wrap an otherwise normal bean
 * whose class is dynamically set at runtime through the PropertyPlaceholderConfigurer
 * because the <bean> element attributes themselves are not parameterizable,
 * only the property values. 
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
public class BeanHolder implements FactoryBean {
    private Class clazz;
    private Object instance;

    public synchronized Object getObject() throws Exception {
        if (this.instance == null) {
            this.instance = this.clazz.newInstance();
        }
        return this.instance;
    }

    public Class getObjectType() {
        return this.clazz;
    }

    public void setObjectType(Class clazz) {
        this.clazz = clazz;
    }

    public boolean isSingleton() {
        return true;
    }
}