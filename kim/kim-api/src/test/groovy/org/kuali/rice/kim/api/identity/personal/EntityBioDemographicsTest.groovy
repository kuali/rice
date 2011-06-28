package org.kuali.rice.kim.api.identity.personal

import junit.framework.Assert
import org.junit.Test
import org.kuali.rice.kim.api.test.JAXBAssert

class EntityBioDemographicsTest {
	private static final String ENTITY_ID = "190192";
    
    private static final String DECEASED_DATE_STRING = "2020-12-25";
    private static final String BIRTH_DATE_STRING = "1980-01-01";
    private static final String GENDER_CODE = "M";
    private static final String MARITAL_STATUS_CODE = "M";
    private static final String PRIMARY_LANGUAGE_CODE = "E";
    private static final String SECONDARY_LANGUAGE_CODE = "S";
    private static final String COUNTRY_OF_BIRTH_CODE = "USA";
    private static final String BIRTH_STATE_CODE = "ST";
    private static final String CITY_OF_BIRTH = "CITY";
    private static final String GEOGRAPHIC_ORIGIN = "Over there";
    private static final String SUPPRESS_PERSONAL = "false";
    
    private static final Long VERSION_NUMBER = new Integer(1);
	private static final String OBJECT_ID = UUID.randomUUID();

    private static final String XML = """
    <entityBioDemographics xmlns="http://rice.kuali.org/kim/v2_0">
        <entityId>${ENTITY_ID}</entityId>
        <deceasedDate>${DECEASED_DATE_STRING}</deceasedDate>
        <birthDate>${BIRTH_DATE_STRING}</birthDate>
        <genderCode>${GENDER_CODE}</genderCode>
        <maritalStatusCode>${MARITAL_STATUS_CODE}</maritalStatusCode>
        <primaryLanguageCode>${PRIMARY_LANGUAGE_CODE}</primaryLanguageCode>
        <secondaryLanguageCode>${SECONDARY_LANGUAGE_CODE}</secondaryLanguageCode>
        <countryOfBirthCode>${COUNTRY_OF_BIRTH_CODE}</countryOfBirthCode>
        <birthStateCode>${BIRTH_STATE_CODE}</birthStateCode>
        <cityOfBirth>${CITY_OF_BIRTH}</cityOfBirth>
        <geographicOrigin>${GEOGRAPHIC_ORIGIN}</geographicOrigin>
        <birthDateUnmasked>${BIRTH_DATE_STRING}</birthDateUnmasked>
        <genderCodeUnmasked>${GENDER_CODE}</genderCodeUnmasked>
        <maritalStatusCodeUnmasked>${MARITAL_STATUS_CODE}</maritalStatusCodeUnmasked>
        <primaryLanguageCodeUnmasked>${PRIMARY_LANGUAGE_CODE}</primaryLanguageCodeUnmasked>
        <secondaryLanguageCodeUnmasked>${SECONDARY_LANGUAGE_CODE}</secondaryLanguageCodeUnmasked>
        <countryOfBirthCodeUnmasked>${COUNTRY_OF_BIRTH_CODE}</countryOfBirthCodeUnmasked>
        <birthStateCodeUnmasked>${BIRTH_STATE_CODE}</birthStateCodeUnmasked>
        <cityOfBirthUnmasked>${CITY_OF_BIRTH}</cityOfBirthUnmasked>
        <geographicOriginUnmasked>${GEOGRAPHIC_ORIGIN}</geographicOriginUnmasked>
        <suppressPersonal>${SUPPRESS_PERSONAL}</suppressPersonal>
        <versionNumber>${VERSION_NUMBER}</versionNumber>
        <objectId>${OBJECT_ID}</objectId>
    </entityBioDemographics>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_entityId_whitespace() {
        EntityBioDemographics.Builder builder = EntityBioDemographics.Builder.create("", "M");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_entityId_null() {
        EntityBioDemographics.Builder builder = EntityBioDemographics.Builder.create(null, "M");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_gender_whitespace() {
        EntityBioDemographics.Builder builder = EntityBioDemographics.Builder.create("10101", "");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_gender_null() {
        EntityBioDemographics.Builder builder = EntityBioDemographics.Builder.create("10101", null);
    }

    @Test
    void test_copy() {
        def o1 = EntityBioDemographics.Builder.create("10101", "M").build();
        def o2 = EntityBioDemographics.Builder.create(o1).build();

        Assert.assertEquals(o1, o2);
    }

    @Test
    void happy_path() {
        EntityBioDemographics.Builder.create("10101", "M");
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, EntityBioDemographics.class)
	}

    public static create() {
		return EntityBioDemographics.Builder.create(new EntityBioDemographicsContract() {
            def String entityId = EntityBioDemographicsTest.ENTITY_ID
            def String deceasedDate = EntityBioDemographicsTest.DECEASED_DATE_STRING
            def String birthDate = EntityBioDemographicsTest.BIRTH_DATE_STRING
            def String genderCode = EntityBioDemographicsTest.GENDER_CODE
            def String maritalStatusCode = EntityBioDemographicsTest.MARITAL_STATUS_CODE
            def String primaryLanguageCode = EntityBioDemographicsTest.PRIMARY_LANGUAGE_CODE
            def String secondaryLanguageCode = EntityBioDemographicsTest.SECONDARY_LANGUAGE_CODE
            def String countryOfBirthCode = EntityBioDemographicsTest.COUNTRY_OF_BIRTH_CODE
            def String cityOfBirthCode = EntityBioDemographicsTest.CITY_OF_BIRTH
            def String birthStateCode = EntityBioDemographicsTest.BIRTH_STATE_CODE
            def String cityOfBirth = EntityBioDemographicsTest.CITY_OF_BIRTH
            def String geographicOrigin = EntityBioDemographicsTest.GEOGRAPHIC_ORIGIN
            def String birthDateUnmasked = EntityBioDemographicsTest.BIRTH_DATE_STRING
            def String genderCodeUnmasked = EntityBioDemographicsTest.GENDER_CODE
            def String maritalStatusCodeUnmasked = EntityBioDemographicsTest.MARITAL_STATUS_CODE
            def String primaryLanguageCodeUnmasked = EntityBioDemographicsTest.PRIMARY_LANGUAGE_CODE
            def String secondaryLanguageCodeUnmasked = EntityBioDemographicsTest.SECONDARY_LANGUAGE_CODE
            def String countryOfBirthCodeUnmasked = EntityBioDemographicsTest.COUNTRY_OF_BIRTH_CODE
            def String cityOfBirthCodeUnmasked = EntityBioDemographicsTest.CITY_OF_BIRTH
            def String birthStateCodeUnmasked = EntityBioDemographicsTest.BIRTH_STATE_CODE
            def String cityOfBirthUnmasked = EntityBioDemographicsTest.CITY_OF_BIRTH
            def String geographicOriginUnmasked = EntityBioDemographicsTest.GEOGRAPHIC_ORIGIN
            def boolean suppressPersonal = EntityBioDemographicsTest.SUPPRESS_PERSONAL.toBoolean()
            def Long versionNumber = EntityBioDemographicsTest.VERSION_NUMBER;
			def String objectId = EntityBioDemographicsTest.OBJECT_ID
        }).build()

	}
    
}
