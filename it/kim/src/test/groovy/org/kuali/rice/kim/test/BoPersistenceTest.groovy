package org.kuali.rice.kim.test

import org.junit.Test;
import org.junit.Before

import org.kuali.rice.krad.service.KRADServiceLocator
import org.kuali.rice.krad.service.BusinessObjectService
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneBo
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneTypeBo
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo
import org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo
import org.kuali.rice.kim.impl.identity.type.EntityTypeContactInfoBo
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo
import org.kuali.rice.kim.impl.identity.personal.EntityBioDemographicsBo
import java.text.SimpleDateFormat
import org.kuali.rice.kim.impl.identity.external.EntityExternalIdentifierTypeBo
import org.kuali.rice.kim.impl.identity.external.EntityExternalIdentifierBo
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationTypeBo
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo
import org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipStatusBo
import org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipBo
import org.kuali.rice.kim.impl.identity.personal.EntityEthnicityBo
import org.kuali.rice.kim.impl.identity.residency.EntityResidencyBo
import org.kuali.rice.kim.impl.identity.visa.EntityVisaBo
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentTypeBo
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusBo
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentBo
import org.kuali.rice.kim.impl.identity.entity.EntityBo
import org.kuali.rice.kim.test.KIMTestCase
import org.kuali.rice.kim.impl.identity.privacy.EntityPrivacyPreferencesBo
import org.kuali.rice.core.api.mo.common.Identifiable
import org.kuali.rice.kim.test.Factory
import org.kuali.rice.kim.test.EntityFactory
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import javax.sql.DataSource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate
import org.junit.Assert
import org.junit.Ignore
import org.kuali.rice.krad.bo.PersistableBusinessObject
import org.kuali.rice.kim.bo.ui.KimDocumentBoActivatableEditableBase
import org.joda.time.DateTime
import java.sql.Timestamp

/**
 * Tests persisting Entity objects in order to verify ORM mappings
 */
abstract class BoPersistenceTest extends KIMTestCase {

    protected BusinessObjectService boService;
    protected factory = new EntityFactory()
    protected def datasource;

    @Before
    void init() {
        boService = (BusinessObjectService) KRADServiceLocator.getBusinessObjectService()
        datasource = (DataSource) GlobalResourceLoader.getService("kimDataSource")
    }

    protected def bool(value, column) {
        [ (column): value ? "Y" : "N" ]
    }
    protected def default_field(bo) {
        bool(bo.dflt, 'DFLT_IND')
    }
    protected def active_field(bo) {
        bool(bo.active, 'ACTV_IND')
    }
    protected def edit_field(bo) {
        bool(bo.edit, 'EDIT_FLAG')
    }
    protected def docno_field(KimDocumentBoActivatableEditableBase bo) {
        [ FDOC_NBR: bo.documentNumber ]
    }
    protected def kimdoc_fields(KimDocumentBoActivatableEditableBase bo) {
        basic_fields(bo) + active_field(bo) + default_field(bo) + edit_field(bo) + docno_field(bo)
    }

    protected def basic_fields(PersistableBusinessObject bo) {
        [ OBJ_ID: bo.objectId,
          VER_NBR: new BigDecimal(bo.versionNumber) ]
    }

    protected def standard_fields(PersistableBusinessObject bo) {
        active_field(bo) + basic_fields(bo)
    }

    protected def toDbTimestamp(DateTime datetime) {
        return toDbTimestamp(datetime.millis)
    }

    protected def toDbTimestamp(long millis) {
        def timestamp = new java.sql.Timestamp(millis)
        timestamp.nanos = 0
        timestamp
    }

    protected def assertRow(Map fields, table, pk="id", ignore=["LAST_UPDT_DT"]) {
        Map row = new SimpleJdbcTemplate(datasource).queryForMap("select * from " + table + " where " + pk + "=?", fields[pk])
        row.keySet().removeAll(ignore)
        /*for (Map.Entry e: fields.entrySet()) {
            println(e.getKey().getClass());
            println(e.getValue().getClass());
            println(e.getKey());
            println(e.getValue());
        }
        for (Map.Entry e: row.entrySet()) {
            println(e.getKey().getClass());
            println(e.getValue().getClass());
            println(e.getKey());
            println(e.getValue());
        }*/
        Assert.assertEquals(new HashMap(fields), new HashMap(row))
    }
}