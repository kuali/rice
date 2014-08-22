package org.kuali.rice.kns.web.struts.form.pojo;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class PojoPropertyUtilsBeanTest {

    @Before
    /**
     * This method sets up BeanUtils so PropertyUtils will delegate to the
     * PojoPropertyUtilsBean like it does with Struts
     */
    public void setup() {
        PojoPlugin.initBeanUtils();
    }

    @Test
    /**
     * This test checks to ensure that the PropertyUtils method which delegates
     * to the PojoPropertyUtilsBean in the KNS properly handles checking if a
     * nested property is writeable.
     */
    public void testNestedPropertyIsWriteable() {
        ReadonlyBean testBean = new ReadonlyBean();
        ReadonlyWrappingBean readonlyBean = new ReadonlyWrappingBean(testBean);
        WriteableWrappingBean writeableBean = new WriteableWrappingBean(testBean);

        assertFalse(PropertyUtils.isWriteable(readonlyBean, "bean.value"));
        assertFalse(PropertyUtils.isWriteable(writeableBean, "bean.value"));
    }

    private static class ReadonlyBean {

        private Boolean value;

        public Boolean getValue() {
            return value;
        }
    }

    private static class ReadonlyWrappingBean {

        private ReadonlyBean bean;

        public ReadonlyWrappingBean(ReadonlyBean bean) {
            this.bean = bean;
        }

        public ReadonlyBean getBean() {
            return bean;
        }
    }

    private static class WriteableWrappingBean {

        private ReadonlyBean bean;

        public WriteableWrappingBean(ReadonlyBean bean) {
            this.bean = bean;
        }

        public ReadonlyBean getBean() {
            return bean;
        }

        public void setBean(ReadonlyBean bean) {
            this.bean = bean;
        }
    }
}
