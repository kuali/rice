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
            documentNumber: System.currentTimeMillis(),
            city: addr.cityUnmasked,
            stateProvinceCode: addr.stateProvinceCodeUnmasked,
            postalCode: addr.postalCodeUnmasked,
            countryCode: addr.countryCodeUnmasked,
            line1: addr.line1Unmasked,
            line2: addr.line2Unmasked,
            line3: addr.line3Unmasked
        ])

        boService.save(pda)

        assertRow(kimdoc_fields(pda) + [
            ENTITY_ADDR_ID: addr.id,
            ADDR_LINE_1: addr.line1Unmasked,
            ADDR_LINE_2: addr.line2Unmasked,
            ADDR_LINE_3: addr.line3Unmasked,
            STATE_PVC_CD: addr.stateProvinceCodeUnmasked,
            POSTAL_CD: addr.postalCodeUnmasked,
            POSTAL_CNTRY_CD: addr.countryCodeUnmasked,
            ADDR_TYP_CD: addr.addressTypeCode,
            CITY: addr.cityUnmasked,
            DISPLAY_SORT_CD: null,
            DFLT_IND: addr.defaultValue ? "Y" : "N",
        ],
        "KRIM_PND_ADDR_MT", "ENTITY_ADDR_ID")
    }
}