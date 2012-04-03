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
package org.kuali.rice.kns.web.struts.form.pojo;

import org.apache.commons.beanutils.NestedNullException;
import org.junit.Test;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PojoPluginTest {

    /**
     * <p>Testing scenario that was not working in the linked issue off of
     * KULRICE-6877: KualiMaintainbleImpl#performCollectionForceUpperCase blowing up</p>
     * 
     * @throws Exception
     */
    @Test
    public void testGetChildCollectionThrowsNestedNullException() throws Exception {

        // We need to initialize PropertyUtils to use our plugins
        new PojoPlugin().init(null, null);

        TestCollectionHolderHolder tchh = new TestCollectionHolderHolder();
        tchh.setTch(new TestCollectionHolder());
        
        // this simulates a situation in which the property (tch) is a proxied object 
        // that can't be fetched, so getting it works (returns the proxy) but trying 
        // to access the collection underneath it throws a NestedNullException
        Object result = ObjectUtils.getPropertyValue(tchh, "tch.collection");

        // before, the empty string was being returned, which doesn't make sense for a collection
        assertFalse("".equals(result));
        // now we return null
        assertTrue(null == result);
    }

    /**
     * Ugly name, but it holds a TestCollectionHolder
     */
    public static class TestCollectionHolderHolder extends PersistableBusinessObjectBase {
        private TestCollectionHolder tch = null;

        public TestCollectionHolder getTch() {
            return tch;
        }

        public void setTch(TestCollectionHolder tch) {
            this.tch = tch;
        }
    }

    /**
     * Test class that holds a collection, but trying to get it results in a
     * NestedNullException.
     * @throws NestedNullException
     */
    public static class TestCollectionHolder extends PersistableBusinessObjectBase {
        private Collection collection = Collections.emptyList();

        public Collection getCollection() {
            throw new NestedNullException();
        }

        public void setCollection(Collection collection) {
            this.collection = collection;
        }
    }

}
