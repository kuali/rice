package org.kuali.rice.kim.impl.identity.name

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

class EntityNameBoPersistenceTest extends KIMTestCase {

    private BusinessObjectService boService;
    private factory = new EntityFactory()
    private def datasource

    @Before
    void init() {
        boService = (BusinessObjectService) KRADServiceLocator.getBusinessObjectService()
        datasource = (DataSource) GlobalResourceLoader.getService("kimDataSource")
    }

    @Test
    void test_save_entity() {
        def entity = Factory.make(EntityBo)
        boService.save(entity)
        assertRow([
            ENTITY_ID: entity.id,
            OBJ_ID: entity.objectId,
            VER_NBR: entity.versionNumber,
            ACTV_IND: entity.active ? "Y" : "N"
        ],
        "KRIM_ENTITY_T", "ENTITY_ID")
    }

    @Test @Ignore
    void test_save_entityname() {
        def entity = Factory.make(EntityBo)
        boService.save(entity)
        def name = Factory.make(EntityNameBo, entity: entity)
        boService.save(name)
        assertRow([id: entity.id],
        "KRIM_ENTITY_T", "ENTITY_ID")
    }

    private def assertRow(Map fields, table, pk="id", ignore=["LAST_UPDT_DT"]) {
        Map row = new SimpleJdbcTemplate(datasource).queryForMap("select * from " + table + " where " + pk + "=?", fields[pk])
        row.keySet().removeAll(ignore)
        Assert.assertEquals(new HashMap(fields), new HashMap(row))
    }
}