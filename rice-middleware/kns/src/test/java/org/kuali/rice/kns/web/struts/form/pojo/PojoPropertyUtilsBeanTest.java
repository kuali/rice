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

    /**
     * This test was added for KULRICE-12283 and is correct, but the fix IU contributed caused an IT to fail
     * so the change was reverted and this test is being ignored until that contribution is finished.
     */
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
