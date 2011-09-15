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
import org.kuali.rice.krad.bo.PersistableBusinessObject

class EntityPersistenceTests extends KIMTestCase {

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

        // assert entity row
        assertRow(standard_fields(entity) + [ ENTITY_ID: entity.id ], "KRIM_ENTITY_T", "ENTITY_ID")

        // assert entity bio demographics row
        assertRow(basic_fields(entity.bioDemographics) + [
            ENTITY_ID: entity.id
        ],
        "KRIM_ENTITY_BIO_T", "ENTITY_ID")
    }

    @Test
    void test_save_entityname() {
        def entity = Factory.make(EntityBo)
        boService.save(entity)
        def name = Factory.make(EntityNameBo, entity: entity)
        boService.save(name)
        assertRow(standard_fields(name) + [
            ENTITY_NM_ID: name.id,
            ENTITY_ID: entity.id,
            FIRST_NM: name.firstName,
            PREFIX_NM: null,
            MIDDLE_NM: null,
            LAST_NM: name.lastName,
            SUFFIX_NM: null,
            NM_TYP_CD: name.nameType.code,
            DFLT_IND: name.defaultValue ? "Y" : "N",
        ],
        "KRIM_ENTITY_NM_T", "ENTITY_NM_ID")

    }

    private def active_field(bo) {
        [ ACTV_IND: bo.active ? "Y" : "N" ]
    }

    private def basic_fields(PersistableBusinessObject bo) {
        [ OBJ_ID: bo.objectId,
          VER_NBR: new BigDecimal(bo.versionNumber) ]
    }

    private def standard_fields(PersistableBusinessObject bo) {
        active_field(bo) + basic_fields(bo)
    }

    private def assertRow(Map fields, table, pk="id", ignore=["LAST_UPDT_DT"]) {
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