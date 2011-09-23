package org.kuali.rice.kim.impl.identity.name

import org.junit.Test
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo
import org.kuali.rice.kim.impl.identity.entity.EntityBo
import org.kuali.rice.kim.impl.identity.type.EntityTypeContactInfoBo
import org.kuali.rice.kim.test.BoPersistenceTest
import org.kuali.rice.kim.test.Factory

/**
 * Tests persisting Entity objects in order to verify ORM mappings
 */
class EntityPersistenceTests extends BoPersistenceTest {
    @Test
    void test_save_entity() {
        EntityBo entity = Factory.make(EntityBo)
        boService.save(entity)

        // assert entity row
        assertRow(standard_fields(entity) + [ ENTITY_ID: entity.id ], "KRIM_ENTITY_T", "ENTITY_ID")

        def bio = entity.bioDemographics
        def born = bio.birthDateValue
        born.clearTime()
        def died = bio.deceasedDateValue
        died.clearTime()

        // assert entity bio demographics row
        assertRow(basic_fields(bio) + [
            ENTITY_ID: entity.id,
            SEC_LANG_CD: bio.secondaryLanguageCode,
            MARITAL_STATUS: bio.maritalStatusCode,
            DECEASED_DT: new java.sql.Timestamp(died.time),
            BIRTH_CITY: bio.birthCity,
            GEO_ORIGIN: bio.geographicOrigin,
            PRIM_LANG_CD: bio.primaryLanguageCode,
            GNDR_CD: bio.genderCode,
            GNDR_CHG_CD: bio.genderChangeCode,
            BIRTH_DT: new java.sql.Timestamp(born.time),
            BIRTH_STATE_PVC_CD: bio.birthStateProvinceCode,
            BIRTH_CNTRY_CD: bio.birthCountry,
            NOTE_MSG: bio.noteMessage
        ],
        "KRIM_ENTITY_BIO_T", "ENTITY_ID")
    }

    @Test
    void test_save_entityname() {
        EntityBo entity = Factory.make(EntityBo)
        boService.save(entity)
        EntityNameBo name = Factory.make(EntityNameBo, entity: entity)
        boService.save(name)

        assertRow(standard_fields(name) + [
            ENTITY_NM_ID: name.id,
            ENTITY_ID: entity.id,
            FIRST_NM: name.firstNameUnmasked,
            TITLE_NM: name.nameTitleUnmasked,
            PREFIX_NM: name.namePrefixUnmasked,
            MIDDLE_NM: name.middleNameUnmasked,
            LAST_NM: name.lastNameUnmasked,
            SUFFIX_NM: name.nameSuffixUnmasked,
            NOTE_MSG: name.noteMessage,
            NM_TYP_CD: name.nameType.code,
            NM_CHNG_DT: toDbTimestamp(name.nameChangedDate),
            DFLT_IND: name.defaultValue ? "Y" : "N",
        ],
        "KRIM_ENTITY_NM_T", "ENTITY_NM_ID")
    }

    @Test
    void test_save_entityaddress() {
        EntityBo entity = Factory.make(EntityBo)
        boService.save(entity)
        boService.save(Factory.make(EntityTypeContactInfoBo, entity: entity))
        EntityAddressBo addr = Factory.make(EntityAddressBo, entity: entity)
        boService.save(addr)
        assertRow(standard_fields(addr) + [
            ENTITY_ADDR_ID: addr.id,
            ENTITY_ID: entity.id,
            ENT_TYP_CD: addr.entityTypeCode,
            ATTN_LINE: addr.attentionLineUnmasked,
            ADDR_LINE_1: addr.line1Unmasked,
            ADDR_LINE_2: addr.line2Unmasked,
            ADDR_LINE_3: addr.line3Unmasked,
            STATE_PVC_CD: addr.stateProvinceCodeUnmasked,
            POSTAL_CD: addr.postalCodeUnmasked,
            POSTAL_CNTRY_CD: addr.countryCodeUnmasked,
            ADDR_TYP_CD: addr.addressTypeCode,
            CITY: addr.cityUnmasked,
            DFLT_IND: addr.defaultValue ? "Y" : "N",
        ],
        "KRIM_ENTITY_ADDR_T", "ENTITY_ADDR_ID")
    }
}