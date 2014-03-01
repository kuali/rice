/*
 * Copyright 2005-2014 The Kuali Foundation
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

import org.eclipse.persistence.exceptions.DatabaseException;
import org.junit.Test;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static org.junit.Assert.*;

/**
 * Tests the DisableVersion annotation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB) // no rollback so we can avoid transactional cache mucking up our results
public class DisableVersioningTest extends KRADTestCase {

    private DisableVersion createDisableVersion(String rndId, String propertyVal) {
        return new DisableVersion(rndId, propertyVal, new Long(0));
    }

    private DisableNoVersion createDisableNoVersion(String rndId, String propertyVal) {
        return new DisableNoVersion(rndId, propertyVal);
    }

    private DisableNoVersionRemoveMapping createDisableNoVersionRemoveMapping(String rndId, String propertyVal) {
        return new DisableNoVersionRemoveMapping(rndId, propertyVal);
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
        KRADServiceLocator.getDataObjectService().save(createDisableVersion(rndId, property));
        // retrieve the object back from the datasource
        DisableVersion resultDV = KRADServiceLocator.getDataObjectService().find(DisableVersion.class, rndId);
        // validate
        assertNotNull("DisableVersion is null", resultDV);
        assertEquals("DisableVersion id does not match given value", rndId, resultDV.getId());
        assertEquals("DisableVersion property does not match given value", property, resultDV.getProperty());
        assertEquals(new Long(0), resultDV.getVersionNumber());
        // now set the version number to a value and make sure it persists properly
        resultDV.setVersionNumber(new Long(50));
        resultDV = KRADServiceLocator.getDataObjectService().save(resultDV);
        assertEquals(new Long(50), resultDV.getVersionNumber());

        // now, since DisableNoVersion has no version number column, it should throw an exception when we attempt to
        // persist it since there is no ver_nbr column in the database
        try {
            KRADServiceLocator.getDataObjectService().save(createDisableNoVersion(rndId, property));
            fail("Database exception should have been thrown when saving with no version number column");
        } catch (DatabaseException e) {}

        // DisableVersionRemoveMapping *should* work though because we are removing the VER_NBR column mapping which
        // should help because we have no such column in the database
        KRADServiceLocator.getDataObjectService().save(createDisableNoVersionRemoveMapping(rndId, property));
        // retrieve the object back from the datasource
        DisableNoVersionRemoveMapping resultDNV = KRADServiceLocator.getDataObjectService().find(DisableNoVersionRemoveMapping.class, rndId);
        // validate
        assertNotNull("DisableNoVersionRemoveMapping is null", resultDNV);
        assertEquals("DisableNoVersionRemoveMapping id does not match given value", rndId, resultDNV.getId());
        assertEquals("DisableNoVersionRemoveMapping property does not match given value", property, resultDNV.getProperty());

    }

    @Entity
    @Table(name="KRTST_TEST_DISABLE_VER_T")
    @DisableVersioning
    public static class DisableVersion extends PersistableBusinessObjectBase {

        @Id
        @Column(name="ID")
        private String id;

        @Column(name="STR_PROP")
        private String property;

        public DisableVersion() { }

        public DisableVersion(String id, String property, Long versionNumber) {
            this.id = id;
            this.property = property;
            setVersionNumber(versionNumber);
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
    @Table(name="KRTST_TEST_DISABLE_NO_VER_T")
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

    @Entity
    @Table(name="KRTST_TEST_DISABLE_NO_VER_T")
    @DisableVersioning
    @RemoveMapping(name = "versionNumber")
    public static class DisableNoVersionRemoveMapping extends PersistableBusinessObjectBase {

        @Id
        @Column(name="ID")
        private String id;

        @Column(name="STR_PROP")
        private String property;

        public DisableNoVersionRemoveMapping() { }

        public DisableNoVersionRemoveMapping(String id, String property) {
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
}
