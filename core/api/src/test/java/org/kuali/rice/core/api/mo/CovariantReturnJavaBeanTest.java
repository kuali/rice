package org.kuali.rice.core.api.mo;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import static org.junit.Assert.*;

public class CovariantReturnJavaBeanTest {

    @Test @Ignore
    public void testBeanPropertyUtils() throws Throwable {
        assertEquals(InnerBean.class, PropertyUtils.getPropertyType(new Bean(), "innerBean"));
    }

    @Test @Ignore
    public void testBeanIntrospector() throws Throwable {
        BeanInfo bi = Introspector.getBeanInfo(Bean.class);
        for (PropertyDescriptor d : bi.getPropertyDescriptors()) {
            if ("innerBean".equals(d.getName())) {
                assertEquals(InnerBean.class, d.getPropertyType());
            }
        }
    }
}