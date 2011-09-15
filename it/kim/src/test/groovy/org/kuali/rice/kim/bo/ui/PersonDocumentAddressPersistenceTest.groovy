package org.kuali.rice.kim.bo.ui

import org.junit.Test
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo
import org.kuali.rice.kim.impl.identity.entity.EntityBo
import org.kuali.rice.kim.impl.identity.type.EntityTypeContactInfoBo
import org.kuali.rice.kim.test.BoPersistenceTest
import org.kuali.rice.kim.test.Factory

/**
 * Tests persisting PersonDocumentAddress object in order to verify ORM mappings
 */
class PersonDocumentAddressPersistenceTest extends BoPersistenceTest {
    @Test
    void test_save_persondocumentaddress() {
        EntityBo entity = Factory.make(EntityBo)
        boService.save(entity)
        EntityTypeContactInfoBo ent_type = Factory.make(EntityTypeContactInfoBo, entity: entity)
        boService.save(ent_type)
        EntityAddressBo addr = Factory.make(EntityAddressBo, entity: entity)
        boService.save(addr)

        PersonDocumentAddress pda = new PersonDocumentAddress([
            entityAddressId: addr.id,
            addressType: addr.addressType,
            addressTypeCode: addr.addressTypeCode,
            entityTypeCode: ent_type.entityTypeCode,
            city: addr.cityUnmasked,
            stateProvinceCode: addr.stateProvinceCodeUnmasked,
            postalCode: addr.postalCodeUnmasked,
            countryCode: addr.countryCodeUnmasked,
            line1: addr.line1Unmasked,
            line2: addr.line2Unmasked,
            line3: addr.line3Unmasked
        ])

        boService.save(pda)

        assertRow(standard_fields(addr) + [
            ENTITY_ADDR_ID: addr.id,
            ENTITY_ID: entity.id,
            ENT_TYP_CD: addr.entityTypeCode,
            ADDR_LINE_1: addr.line1Unmasked,
            ADDR_LINE_2: addr.line2Unmasked,
            ADDR_LINE_3: addr.line3Unmasked,
            STATE_PVC_CD: addr.stateProvinceCodeUnmasked,
            POSTAL_CD: addr.postalCodeUnmasked,
            POSTAL_CNTRY_CD: addr.countryCodeUnmasked,
            ADDR_TYP_CD: addr.addressTypeCode,
            CITY_NM: addr.cityUnmasked,
            DFLT_IND: addr.defaultValue ? "Y" : "N",
        ],
        "KRIM_PND_ADDR_MT", "ENTITY_ADDR_ID")

        NM_TYP_CD=namecodeone, DFLT_IND=N, ENTITY_ID=132662f703d, $column=Y, PREFIX_NM=null, ENTITY_NM_ID=132662f7040}>


        NM_TYP_CD=namecodeone, DFLT_IND=N, ENTITY_ID=132662f703d, ACTV_IND=Y, PREFIX_NM=null, ENTITY_NM_ID=132662f7040}>
  test_save_entityaddress(org.kuali.rice.kim.impl.identity.name.EntityPersistenceTests): expected:<{ADDR_LINE_3=null, VER_NBR=1, ENTITY_ADDR_ID=132662f7044, POSTAL_CD=null, ADDR_LINE_1=null, STATE_PVC_CD=null, ADDR_LINE_2=null, OBJ_ID=162f364b-6423-41e3-b48b-7e0f5c8e350d, POSTAL_CNTRY_CD=null, ADDR_TYP_CD=HM, CITY_NM=null, DFLT_IND=N, ENTITY_ID=132662f7041, $column=Y, ENT_TYP_CD=PERSON}> but was:<{ADDR_LINE_3=null, VER_NBR=1, ENTITY_ADDR_ID=132662f7044, POSTAL_CD=null, ADDR_LINE_1=null, STATE_PVC_CD=null, ADDR_LINE_2=null, OBJ_ID=162f364b-6423-41e3-b48b-7e0f5c8e350d, POSTAL_CNTRY_CD=null, ADDR_TYP_CD=HM, CITY_NM=null, DFLT_IND=N, ENTITY_ID=132662f7041, ACTV_IND=Y, ENT_TYP_CD=PERSON}
    }
}