/*
 * Copyright 2006-2013 The Kuali Foundation
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

package org.kuali.rice.krad.data.jpa;

import org.junit.Test;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the DisableVersion annotation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DisableVersioningTest extends KRADTestCase {

    @Entity
    @Table(name="KRTST_TEST_DISABLE_VER_TABLE_T")
    @DisableVersioning
    public static class DisableVersion extends PersistableBusinessObjectBase {

        @Id
        @Column(name="ID")
        private String id;

        @Column(name="STR_PROP")
        private String property;

        public DisableVersion() { }

        public DisableVersion(String id, String property) {
            this.id = id;
            this.property = property;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }

    @Entity
    @Table(name="KRTST_TEST_DISABLE_NO_VER_TABLE_T")
    @DisableVersioning
    public static class DisableNoVersion extends PersistableBusinessObjectBase {

        @Id
        @Column(name="ID")
        private String id;

        @Column(name="STR_PROP")
        private String property;

        public DisableNoVersion() { }

        public DisableNoVersion(String id, String property) {
            this.id = id;
            this.property = property;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }

    private DisableVersion createDisableVersion(String rndId, String propertyVal) {
        return new DisableVersion(rndId, propertyVal);
    }

    private DisableNoVersion createDisableNoVersion(String rndId, String propertyVal) {
        return new DisableNoVersion(rndId, propertyVal);
    }

    private String getRandomId() {
        String time = String.valueOf(System.currentTimeMillis());
        return (time.substring(time.length() - 9));
    }

    @Test
    public void testDisableVersioning() {
        // get a random value for our id
        String rndId = getRandomId();
        String property = "testPropertyValue";

        // persist to the datasource
        KRADServiceLocator.getDataObjectService().save(createDisableNoVersion(rndId, property));
        // retrieve the object back from the datasource
        DisableNoVersion resultDNV = KRADServiceLocator.getDataObjectService().find(DisableNoVersion.class, rndId);
        // validate
        assertNotNull("DisableNoVersion is null", resultDNV);
        assertEquals("DisableNoVersion id does not match given value", rndId, resultDNV.getId());
        assertEquals("DisableNoVersion property does not match given value", property, resultDNV.getProperty());

        // persist to the datasource
        KRADServiceLocator.getDataObjectService().save(createDisableVersion(rndId, property));
        // retrieve the object back from the datasource
        DisableVersion resultDV = KRADServiceLocator.getDataObjectService().find(DisableVersion.class, rndId);
        // validate
        assertNotNull("DisableVersion is null", resultDV);
        assertEquals("DisableVersion id does not match given value", rndId, resultDV.getId());
        assertEquals("DisableVersion property does not match given value", property, resultDV.getProperty());
    }
}
