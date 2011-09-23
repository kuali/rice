package org.kuali.rice.kim.test

import org.kuali.rice.kim.api.KimConstants
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo
import org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationTypeBo
import org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipBo
import org.kuali.rice.kim.impl.identity.citizenship.EntityCitizenshipStatusBo
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentBo
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusBo
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentTypeBo
import org.kuali.rice.kim.impl.identity.entity.EntityBo
import org.kuali.rice.kim.impl.identity.external.EntityExternalIdentifierBo
import org.kuali.rice.kim.impl.identity.external.EntityExternalIdentifierTypeBo
import org.kuali.rice.kim.impl.identity.name.EntityNameBo
import org.kuali.rice.kim.impl.identity.name.EntityNameTypeBo
import org.kuali.rice.kim.impl.identity.personal.EntityBioDemographicsBo
import org.kuali.rice.kim.impl.identity.personal.EntityEthnicityBo
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneBo
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneTypeBo
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo
import org.kuali.rice.kim.impl.identity.privacy.EntityPrivacyPreferencesBo
import org.kuali.rice.kim.impl.identity.residency.EntityResidencyBo
import org.kuali.rice.kim.impl.identity.type.EntityTypeContactInfoBo
import org.kuali.rice.kim.impl.identity.visa.EntityVisaBo

/**
 * Factory for constructing Entity- objects
 * TODO: ensure valid default data and type codes
 */
class EntityFactory extends Factory {
     def EntityPrivacyPreferencesBo(Map fields) {
        new EntityPrivacyPreferencesBo(Factory.mergeAndLink('entity', fields))
    }

    def EntityBioDemographicsBo(Map fields) {
        def now = new java.sql.Date(new Date().time)
        def values = [
            birthDateValue: now,
            genderCode: "M",
            deceasedDateValue: new java.sql.Date((long) (now.time + (1000L * 60 * 60 * 24 * 365 * 80))),
            maritalStatusCode: "S",
            primaryLanguageCode: "EN",
            secondaryLanguageCode: "FR",
            birthCountry: "US",
            birthStateProvinceCode: "IN",
            birthCity: "Bloomington",
            geographicOrigin: "None",
            noteMessage: "note message",
            suppressPersonal: false
        ]
        new EntityBioDemographicsBo(Factory.mergeAndLink('entity', fields, values))
    }

    def PrincipalBo(Map fields) {
        def values = [
            principalId: Factory.makeId(), active: true, principalName: "first" + Factory.makeId(), password: "first_password"
        ]
        new PrincipalBo(Factory.mergeAndLink('entity', fields, values));
    }

    def EntityTypeContactInfoBo(Map fields) {
        new EntityTypeContactInfoBo(Factory.mergeAndLink('entity', fields, [ entityTypeCode: KimConstants.EntityTypes.PERSON, active: true ]))
    }

    def EntityAddressBo(Map fields) {
        def values = [
           entityTypeCode: KimConstants.EntityTypes.PERSON, addressType: new EntityAddressTypeBo(code: KimConstants.AddressTypes.HOME), id: Factory.makeId(), addressTypeCode: KimConstants.AddressTypes.HOME, active: true
        ]
        new EntityAddressBo(Factory.mergeAndLink('entity', fields, values))
    }

    def EntityEmailBo(Map fields) {
        def values = [
            entityTypeCode: "typecodeone", emailType: new EntityEmailTypeBo(code: "emailcodeone"), id: Factory.makeId(), emailTypeCode: "emailcodeone", active: true
        ]
        new EntityEmailBo(Factory.mergeAndLink('entity', fields, values))
    }

    def EntityPhoneBo(Map fields) {
        def values = [
            entityTypeCode: "typecodeone", phoneType: new EntityPhoneTypeBo(code: "phonecodeone"), id: Factory.makeId(), phoneTypeCode: "phonetypecodeone", active: true
        ]
        new EntityPhoneBo(Factory.mergeAndLink('entity', fields, values))
    }

    def EntityExternalIdentifierBo(Map fields) {
        def values = [
            externalIdentifierType: new EntityExternalIdentifierTypeBo(code: "exidtypecodeone"), id: Factory.makeId(), externalIdentifierTypeCode: "exidtypecodeone"
        ]
        new EntityExternalIdentifierBo(Factory.mergeAndLink('entity', fields, values))
    };

    def EntityAffiliationBo(Map fields) {
        def values = [
            affiliationType: new EntityAffiliationTypeBo(code: "affiliationcodeone"), id: Factory.makeId(), affiliationTypeCode: "affiliationcodeone", active: true
        ]
        new EntityAffiliationBo(Factory.mergeAndLink('entity', fields, values))
    };

    def EntityCitizenshipBo(Map fields) {
        def values = [
            id: Factory.makeId(), active: true, status:  new EntityCitizenshipStatusBo(code: "statuscodeone", name: "statusnameone"), statusCode: "statuscodeone"
        ]
        new EntityCitizenshipBo(Factory.mergeAndLink('entity', fields, values))
    }

    def EntityEthnicityBo(Map fields) {
        new EntityEthnicityBo(Factory.mergeAndLink('entity', fields, [ id: Factory.makeId() ]))
    }

    def EntityResidencyBo(Map fields) {
        new EntityResidencyBo(Factory.mergeAndLink('entity', fields, [ id: Factory.makeId() ]))
    }

    def EntityVisaBo(Map fields) {
        new EntityVisaBo(mergeAndLink('entity', fields, [ id: Factory.makeId() ]))
    };

    def EntityNameBo(Map fields) {
        def values = [
            id: Factory.makeId(), active: true, nameTitle: "DVM", namePrefix: "Mr.", firstName: "John", lastName: "Smith", nameSuffix: "Jr.", noteMessage: "note message", nameType: new EntityNameTypeBo(code: KimConstants.NameTypes.PRIMARY), nameCode: KimConstants.NameTypes.PRIMARY
        ]
        new EntityNameBo(mergeAndLink('entity', fields, values))
    };

    def EntityEmploymentBo(Map fields) {
        def values = [
           id: Factory.makeId(), entityAffiliation: EntityAffiliationBo(fields), employeeType: new EntityEmploymentTypeBo(code: "employmenttypecodeone"), employeeTypeCode: "employmenttypecodeone", employeeStatus: new EntityEmploymentStatusBo(code: "employmentstatusone"), employeeStatusCode: "employmentstatusone", active: true
        ]
        values = Factory.mergeAndLink('entityAffiliation', values, [:])
        new EntityEmploymentBo(Factory.mergeAndLink('entity', fields, values))
    };

    def EntityBo(Map fields) {
        def id = Factory.makeId()
        List<PrincipalBo> princs = new ArrayList<PrincipalBo>()
        princs.add(PrincipalBo(entityId: id))
        def values = [
            active: true, id: id, privacyPreferences: EntityPrivacyPreferencesBo(entityId: id),
            bioDemographics: EntityBioDemographicsBo(entityId: id),
            principals: princs
        ]
        fields.putAll(values)
        new EntityBo(fields)
    }
}
