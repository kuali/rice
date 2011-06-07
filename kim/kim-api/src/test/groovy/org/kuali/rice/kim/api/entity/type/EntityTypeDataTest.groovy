package org.kuali.rice.kim.api.entity.type

import org.junit.Test
import org.junit.Assert
import org.kuali.rice.kim.api.test.JAXBAssert
import org.kuali.rice.kim.api.entity.Type
import org.kuali.rice.kim.api.entity.TypeContract
import org.kuali.rice.kim.api.entity.address.EntityAddress
import org.kuali.rice.kim.api.entity.address.EntityAddressContract
import org.kuali.rice.kim.api.entity.email.EntityEmail
import org.kuali.rice.kim.api.entity.phone.EntityPhoneContract
import org.kuali.rice.kim.api.entity.phone.EntityPhone
import org.kuali.rice.kim.api.entity.email.EntityEmailContract

class EntityTypeDataTest {


	private static final String ENTITY_TYPE_CODE = "PERSON";
    private static final String ENTITY_TYPE_NAME = "PERSON";
	private static final String ENTITY_ID = "190192";
    private static final String TYPE_SORT_CODE = "0"
    private static final String TYPE_ACTIVE = "true"
    private static final Long TYPE_VERSION_NUMBER = new Integer(1)
	private static final String TYPE_OBJECT_ID = UUID.randomUUID()
    private static final String DEFAULT = "true"

    private static final String TYPE_CODE = "Home"
    private static final String TYPE_NAME = "Home-y"



    // ADDRESS stuff
    private static final String ADDR_ID = "1";
    private static final Long ADDR_TYPE_VERSION_NUMBER = new Integer(1)
	private static final String ADDR_TYPE_OBJECT_ID = UUID.randomUUID()
	private static final String ADDR_LINE1 = "Line 1";
    private static final String ADDR_LINE2 = "Line 2";
    private static final String ADDR_LINE3 = "Line 3";
    private static final String ADDR_CITY_NAME = "Super Sweet City";
	private static final String ADDR_STATE_CODE = "CA";
	private static final String ADDR_POSTAL_CODE = "55555"
    private static final String ADDR_COUNTRY_CODE = "USA"
    private static final String ADDR_SUPPRESS = "false"
    private static final String ADDR_DEFAULT = "true"
    private static final String ADDR_ACTIVE = "true"
    private static final Long ADDR_VERSION_NUMBER = new Integer(1);
	private static final String ADDR_OBJECT_ID = UUID.randomUUID();


    //PHONE stuff
    private static final String PHONE_ID = "1";
	private static final String PHONE_NUMBER = "439-0116";
    private static final String PHONE_COUNTRY_CODE = "1";
    private static final String PHONE_EXTENSION_NUMBER ="12"
    private static final String PHONE_SUPPRESS = "false"
    private static final String PHONE_ACTIVE = "true"
    private static final Long PHONE_VERSION_NUMBER = new Integer(1);
    private static final String PHONE_DEFAULT = "true";
	private static final String PHONE_OBJECT_ID = UUID.randomUUID();
    private static final Long PHONE_TYPE_VERSION_NUMBER = new Integer(1)
	private static final String PHONE_TYPE_OBJECT_ID = UUID.randomUUID()

    //email data
    private static final String EMAIL_ID = "1";
    private static final Long EMAIL_TYPE_VERSION_NUMBER = new Integer(1)
	private static final String EMAIL_TYPE_OBJECT_ID = UUID.randomUUID()
	private static final String EMAIL_ADDRESS = "test@kuali.org";
    private static final String EMAIL_SUPPRESS = "false"
    private static final String EMAIL_DEFAULT = "true"
    private static final String EMAIL_ACTIVE = "true"
    private static final Long EMAIL_VERSION_NUMBER = new Integer(1);
	private static final String EMAIL_OBJECT_ID = UUID.randomUUID();


    private static final Long VERSION_NUMBER = new Integer(1);
	private static final String OBJECT_ID = UUID.randomUUID();
    private static final String ACTIVE = "true";

    private static final String XML = """
    <entityTypeData xmlns="http://rice.kuali.org/kim/v2_0">
        <entityId>${ENTITY_ID}</entityId>
        <entityTypeCode>${ENTITY_TYPE_CODE}</entityTypeCode>
        <entityType>
            <code>${ENTITY_TYPE_CODE}</code>
            <name>${ENTITY_TYPE_CODE}</name>
            <active>${TYPE_ACTIVE}</active>
            <sortCode>${TYPE_SORT_CODE}</sortCode>
            <versionNumber>${TYPE_VERSION_NUMBER}</versionNumber>
            <objectId>${TYPE_OBJECT_ID}</objectId>
        </entityType>
        <addresses>
          <address>
            <id>${ADDR_ID}</id>
            <entityTypeCode>${ENTITY_TYPE_CODE}</entityTypeCode>
            <entityId>${ENTITY_ID}</entityId>
            <addressType>
                <code>${TYPE_CODE}</code>
                <name>${TYPE_NAME}</name>
                <active>${TYPE_ACTIVE}</active>
                <sortCode>${TYPE_SORT_CODE}</sortCode>
                <versionNumber>${ADDR_TYPE_VERSION_NUMBER}</versionNumber>
                <objectId>${ADDR_TYPE_OBJECT_ID}</objectId>
            </addressType>
            <line1>${ADDR_LINE1}</line1>
            <line2>${ADDR_LINE2}</line2>
            <line3>${ADDR_LINE3}</line3>
            <cityName>${ADDR_CITY_NAME}</cityName>
            <stateCode>${ADDR_STATE_CODE}</stateCode>
            <postalCode>${ADDR_POSTAL_CODE}</postalCode>
            <countryCode>${ADDR_COUNTRY_CODE}</countryCode>
            <line1Unmasked>${ADDR_LINE1}</line1Unmasked>
            <line2Unmasked>${ADDR_LINE2}</line2Unmasked>
            <line3Unmasked>${ADDR_LINE3}</line3Unmasked>
            <cityNameUnmasked>${ADDR_CITY_NAME}</cityNameUnmasked>
            <stateCodeUnmasked>${ADDR_STATE_CODE}</stateCodeUnmasked>
            <postalCodeUnmasked>${ADDR_POSTAL_CODE}</postalCodeUnmasked>
            <countryCodeUnmasked>${ADDR_COUNTRY_CODE}</countryCodeUnmasked>
            <defaultValue>${ADDR_DEFAULT}</defaultValue>
            <active>${ADDR_ACTIVE}</active>
            <versionNumber>${ADDR_VERSION_NUMBER}</versionNumber>
            <objectId>${ADDR_OBJECT_ID}</objectId>
          </address>
        </addresses>
        <emailAddresses>
          <emailAddress>
            <id>${EMAIL_ID}</id>
            <entityTypeCode>${ENTITY_TYPE_CODE}</entityTypeCode>
            <entityId>${ENTITY_ID}</entityId>
            <emailType>
                <code>${TYPE_CODE}</code>
                <name>${TYPE_NAME}</name>
                <active>${TYPE_ACTIVE}</active>
                <sortCode>${TYPE_SORT_CODE}</sortCode>
                <versionNumber>${EMAIL_TYPE_VERSION_NUMBER}</versionNumber>
                <objectId>${EMAIL_TYPE_OBJECT_ID}</objectId>
            </emailType>
            <emailAddress>${EMAIL_ADDRESS}</emailAddress>
            <emailAddressUnmasked>${EMAIL_ADDRESS}</emailAddressUnmasked>
            <suppressEmail>${EMAIL_SUPPRESS}</suppressEmail>
            <defaultValue>${EMAIL_DEFAULT}</defaultValue>
            <active>${EMAIL_ACTIVE}</active>
            <versionNumber>${EMAIL_VERSION_NUMBER}</versionNumber>
            <objectId>${EMAIL_OBJECT_ID}</objectId>
          </emailAddress>
        </emailAddresses>
        <phoneNumbers>
          <phoneNumber>
            <id>${PHONE_ID}</id>
            <entityTypeCode>${ENTITY_TYPE_CODE}</entityTypeCode>
            <entityId>${ENTITY_ID}</entityId>
            <phoneType>
                <code>${TYPE_CODE}</code>
                <name>${TYPE_NAME}</name>
                <active>${TYPE_ACTIVE}</active>
                <sortCode>${TYPE_SORT_CODE}</sortCode>
                <versionNumber>${PHONE_TYPE_VERSION_NUMBER}</versionNumber>
                <objectId>${PHONE_TYPE_OBJECT_ID}</objectId>
            </phoneType>
            <countryCode>${PHONE_COUNTRY_CODE}</countryCode>
            <phoneNumber>${PHONE_NUMBER}</phoneNumber>
            <extensionNumber>${PHONE_EXTENSION_NUMBER}</extensionNumber>
            <formattedPhoneNumber>${PHONE_NUMBER} x${PHONE_EXTENSION_NUMBER}</formattedPhoneNumber>
            <countryCodeUnmasked>${PHONE_COUNTRY_CODE}</countryCodeUnmasked>
            <phoneNumberUnmasked>${PHONE_NUMBER}</phoneNumberUnmasked>
            <extensionNumberUnmasked>${PHONE_EXTENSION_NUMBER}</extensionNumberUnmasked>
            <formattedPhoneNumberUnmasked>${PHONE_NUMBER} x${PHONE_EXTENSION_NUMBER}</formattedPhoneNumberUnmasked>
            <suppressPhone>${PHONE_SUPPRESS}</suppressPhone>
            <defaultValue>${PHONE_DEFAULT}</defaultValue>
            <active>${PHONE_ACTIVE}</active>
            <versionNumber>${PHONE_VERSION_NUMBER}</versionNumber>
            <objectId>${PHONE_OBJECT_ID}</objectId>
          </phoneNumber>
        </phoneNumbers>
        <defaultAddress>
            <id>${ADDR_ID}</id>
            <entityTypeCode>${ENTITY_TYPE_CODE}</entityTypeCode>
            <entityId>${ENTITY_ID}</entityId>
            <addressType>
                <code>${TYPE_CODE}</code>
                <name>${TYPE_NAME}</name>
                <active>${TYPE_ACTIVE}</active>
                <sortCode>${TYPE_SORT_CODE}</sortCode>
                <versionNumber>${ADDR_TYPE_VERSION_NUMBER}</versionNumber>
                <objectId>${ADDR_TYPE_OBJECT_ID}</objectId>
            </addressType>
            <line1>${ADDR_LINE1}</line1>
            <line2>${ADDR_LINE2}</line2>
            <line3>${ADDR_LINE3}</line3>
            <cityName>${ADDR_CITY_NAME}</cityName>
            <stateCode>${ADDR_STATE_CODE}</stateCode>
            <postalCode>${ADDR_POSTAL_CODE}</postalCode>
            <countryCode>${ADDR_COUNTRY_CODE}</countryCode>
            <line1Unmasked>${ADDR_LINE1}</line1Unmasked>
            <line2Unmasked>${ADDR_LINE2}</line2Unmasked>
            <line3Unmasked>${ADDR_LINE3}</line3Unmasked>
            <cityNameUnmasked>${ADDR_CITY_NAME}</cityNameUnmasked>
            <stateCodeUnmasked>${ADDR_STATE_CODE}</stateCodeUnmasked>
            <postalCodeUnmasked>${ADDR_POSTAL_CODE}</postalCodeUnmasked>
            <countryCodeUnmasked>${ADDR_COUNTRY_CODE}</countryCodeUnmasked>
            <defaultValue>${ADDR_DEFAULT}</defaultValue>
            <active>${ADDR_ACTIVE}</active>
            <versionNumber>${ADDR_VERSION_NUMBER}</versionNumber>
            <objectId>${ADDR_OBJECT_ID}</objectId>
        </defaultAddress>
        <defaultEmailAddress>
            <id>${EMAIL_ID}</id>
            <entityTypeCode>${ENTITY_TYPE_CODE}</entityTypeCode>
            <entityId>${ENTITY_ID}</entityId>
            <emailType>
                <code>${TYPE_CODE}</code>
                <name>${TYPE_NAME}</name>
                <active>${TYPE_ACTIVE}</active>
                <sortCode>${TYPE_SORT_CODE}</sortCode>
                <versionNumber>${EMAIL_TYPE_VERSION_NUMBER}</versionNumber>
                <objectId>${EMAIL_TYPE_OBJECT_ID}</objectId>
            </emailType>
            <emailAddress>${EMAIL_ADDRESS}</emailAddress>
            <emailAddressUnmasked>${EMAIL_ADDRESS}</emailAddressUnmasked>
            <suppressEmail>${EMAIL_SUPPRESS}</suppressEmail>
            <defaultValue>${EMAIL_DEFAULT}</defaultValue>
            <active>${EMAIL_ACTIVE}</active>
            <versionNumber>${EMAIL_VERSION_NUMBER}</versionNumber>
            <objectId>${EMAIL_OBJECT_ID}</objectId>
        </defaultEmailAddress>
        <defaultPhoneNumber>
            <id>${PHONE_ID}</id>
            <entityTypeCode>${ENTITY_TYPE_CODE}</entityTypeCode>
            <entityId>${ENTITY_ID}</entityId>
            <phoneType>
                <code>${TYPE_CODE}</code>
                <name>${TYPE_NAME}</name>
                <active>${TYPE_ACTIVE}</active>
                <sortCode>${TYPE_SORT_CODE}</sortCode>
                <versionNumber>${PHONE_TYPE_VERSION_NUMBER}</versionNumber>
                <objectId>${PHONE_TYPE_OBJECT_ID}</objectId>
            </phoneType>
            <countryCode>${PHONE_COUNTRY_CODE}</countryCode>
            <phoneNumber>${PHONE_NUMBER}</phoneNumber>
            <extensionNumber>${PHONE_EXTENSION_NUMBER}</extensionNumber>
            <formattedPhoneNumber>${PHONE_NUMBER} x${PHONE_EXTENSION_NUMBER}</formattedPhoneNumber>
            <countryCodeUnmasked>${PHONE_COUNTRY_CODE}</countryCodeUnmasked>
            <phoneNumberUnmasked>${PHONE_NUMBER}</phoneNumberUnmasked>
            <extensionNumberUnmasked>${PHONE_EXTENSION_NUMBER}</extensionNumberUnmasked>
            <formattedPhoneNumberUnmasked>${PHONE_NUMBER} x${PHONE_EXTENSION_NUMBER}</formattedPhoneNumberUnmasked>
            <suppressPhone>${PHONE_SUPPRESS}</suppressPhone>
            <defaultValue>${PHONE_DEFAULT}</defaultValue>
            <active>${PHONE_ACTIVE}</active>
            <versionNumber>${PHONE_VERSION_NUMBER}</versionNumber>
            <objectId>${PHONE_OBJECT_ID}</objectId>
        </defaultPhoneNumber>
        <versionNumber>${VERSION_NUMBER}</versionNumber>
        <objectId>${OBJECT_ID}</objectId>
        <active>${ACTIVE}</active>
    </entityTypeData>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_entityId_whitespace() {
        EntityTypeData.Builder builder = EntityTypeData.Builder.create("", "PERSON");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_entityId_null() {
        EntityTypeData.Builder builder = EntityTypeData.Builder.create(null, "PERSON");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_entityTypeCode_whitespace() {
        EntityTypeData.Builder builder = EntityTypeData.Builder.create("9012923", "");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_entityTypeCode_null() {
        EntityTypeData.Builder builder = EntityTypeData.Builder.create("9012923", null);
    }

    @Test
    void test_copy() {
        def o1 = EntityTypeData.Builder.create("1010101", "SYSTEM").build();
        def o2 = EntityTypeData.Builder.create(o1).build();

        Assert.assertEquals(o1, o2);
    }

    @Test
    void happy_path() {
        EntityTypeData.Builder.create("1010101", "PERSON");
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, EntityTypeData.class)
	}

    private create() {

        EntityAddress addr = EntityAddress.Builder.create(new EntityAddressContract() {
                    def String id = EntityTypeDataTest.ADDR_ID
                    def String entityTypeCode = EntityTypeDataTest.ENTITY_TYPE_CODE
                    def String entityId = EntityTypeDataTest.ENTITY_ID
                    def Type getAddressType() { Type.Builder.create(new TypeContract() {
                        def String code = EntityTypeDataTest.TYPE_CODE
                        def String name = EntityTypeDataTest.TYPE_NAME
                        def boolean active = EntityTypeDataTest.TYPE_ACTIVE.toBoolean()
                        def String sortCode = EntityTypeDataTest.TYPE_SORT_CODE
                        def Long versionNumber = EntityTypeDataTest.ADDR_TYPE_VERSION_NUMBER
                        def String objectId = EntityTypeDataTest.ADDR_TYPE_OBJECT_ID
                    }).build()
                    }
                    def String line1 = EntityTypeDataTest.ADDR_LINE1
                    def String line2 = EntityTypeDataTest.ADDR_LINE2
                    def String line3 = EntityTypeDataTest.ADDR_LINE3
                    def String cityName = EntityTypeDataTest.ADDR_CITY_NAME
                    def String stateCode = EntityTypeDataTest.ADDR_STATE_CODE
                    def String postalCode = EntityTypeDataTest.ADDR_POSTAL_CODE
                    def String countryCode = EntityTypeDataTest.ADDR_COUNTRY_CODE
                    def String line1Unmasked = EntityTypeDataTest.ADDR_LINE1
                    def String line2Unmasked = EntityTypeDataTest.ADDR_LINE2
                    def String line3Unmasked = EntityTypeDataTest.ADDR_LINE3
                    def String cityNameUnmasked = EntityTypeDataTest.ADDR_CITY_NAME
                    def String stateCodeUnmasked = EntityTypeDataTest.ADDR_STATE_CODE
                    def String postalCodeUnmasked = EntityTypeDataTest.ADDR_POSTAL_CODE
                    def String countryCodeUnmasked = EntityTypeDataTest.ADDR_COUNTRY_CODE
                    def boolean suppressAddress = EntityTypeDataTest.ADDR_SUPPRESS.toBoolean()
                    def boolean defaultValue = EntityTypeDataTest.ADDR_DEFAULT.toBoolean()
                    def boolean active = EntityTypeDataTest.ADDR_ACTIVE.toBoolean()
                    def Long versionNumber = EntityTypeDataTest.ADDR_VERSION_NUMBER;
                    def String objectId = EntityTypeDataTest.ADDR_OBJECT_ID
                }).build()

        EntityEmail email = EntityEmail.Builder.create(new EntityEmailContract() {
                    def String id = EntityTypeDataTest.EMAIL_ID
                    def String entityTypeCode = EntityTypeDataTest.ENTITY_TYPE_CODE
                    def String entityId = EntityTypeDataTest.ENTITY_ID
                    def Type getEmailType() { Type.Builder.create(new TypeContract() {
                        def String code = EntityTypeDataTest.TYPE_CODE
                        def String name = EntityTypeDataTest.TYPE_NAME
                        def boolean active = EntityTypeDataTest.TYPE_ACTIVE.toBoolean()
                        def String sortCode = EntityTypeDataTest.TYPE_SORT_CODE
                        def Long versionNumber = EntityTypeDataTest.EMAIL_TYPE_VERSION_NUMBER
                        def String objectId = EntityTypeDataTest.EMAIL_TYPE_OBJECT_ID
                    }).build()}
                    def String emailAddress = EntityTypeDataTest.EMAIL_ADDRESS
                    def String emailAddressUnmasked = EntityTypeDataTest.EMAIL_ADDRESS
                    def boolean suppressEmail = EntityTypeDataTest.EMAIL_SUPPRESS.toBoolean()
                    def boolean defaultValue = EntityTypeDataTest.EMAIL_DEFAULT.toBoolean()
                    def boolean active = EntityTypeDataTest.EMAIL_ACTIVE.toBoolean()
                    def Long versionNumber = EntityTypeDataTest.EMAIL_VERSION_NUMBER;
                    def String objectId = EntityTypeDataTest.EMAIL_OBJECT_ID
                }).build()

        EntityPhone phone = EntityPhone.Builder.create(new EntityPhoneContract() {
                    def String id = EntityTypeDataTest.PHONE_ID
                    def String entityTypeCode = EntityTypeDataTest.ENTITY_TYPE_CODE
                    def String entityId = EntityTypeDataTest.ENTITY_ID
                    def Type getPhoneType() { Type.Builder.create(new TypeContract() {
                        def String code = EntityTypeDataTest.TYPE_CODE
                        def String name = EntityTypeDataTest.TYPE_NAME
                        def boolean active = EntityTypeDataTest.TYPE_ACTIVE.toBoolean()
                        def String sortCode = EntityTypeDataTest.TYPE_SORT_CODE
                        def Long versionNumber = EntityTypeDataTest.PHONE_TYPE_VERSION_NUMBER
                        def String objectId = EntityTypeDataTest.PHONE_TYPE_OBJECT_ID
                    }).build()}
                    def String countryCode = EntityTypeDataTest.PHONE_COUNTRY_CODE
                    def String phoneNumber = EntityTypeDataTest.PHONE_NUMBER
                    def String extensionNumber = EntityTypeDataTest.PHONE_EXTENSION_NUMBER
                    def String countryCodeUnmasked = EntityTypeDataTest.PHONE_COUNTRY_CODE
                    def String phoneNumberUnmasked = EntityTypeDataTest.PHONE_NUMBER
                    def String extensionNumberUnmasked = EntityTypeDataTest.PHONE_EXTENSION_NUMBER
                    def String formattedPhoneNumber = EntityTypeDataTest.PHONE_NUMBER + " x" + EntityTypeDataTest.PHONE_EXTENSION_NUMBER
                    def String formattedPhoneNumberUnmasked = EntityTypeDataTest.PHONE_NUMBER + " x" + EntityTypeDataTest.PHONE_EXTENSION_NUMBER
                    def boolean suppressPhone = EntityTypeDataTest.PHONE_SUPPRESS.toBoolean()
                    def boolean defaultValue = EntityTypeDataTest.PHONE_DEFAULT.toBoolean()
                    def boolean active = EntityTypeDataTest.PHONE_ACTIVE.toBoolean()
                    def Long versionNumber = EntityTypeDataTest.PHONE_VERSION_NUMBER;
                    def String objectId = EntityTypeDataTest.PHONE_OBJECT_ID
                }).build()

		return EntityTypeData.Builder.create(new EntityTypeDataContract() {
            def String entityTypeCode = EntityTypeDataTest.ENTITY_TYPE_CODE
            def String entityId = EntityTypeDataTest.ENTITY_ID
			def Type getEntityType() { Type.Builder.create(new TypeContract() {
				def String code = EntityTypeDataTest.ENTITY_TYPE_CODE
				def String name = EntityTypeDataTest.ENTITY_TYPE_NAME
				def boolean active = EntityTypeDataTest.TYPE_ACTIVE
                def String sortCode = EntityTypeDataTest.TYPE_SORT_CODE
                def Long versionNumber = EntityTypeDataTest.TYPE_VERSION_NUMBER
				def String objectId = EntityTypeDataTest.TYPE_OBJECT_ID
			}).build()}
            def List<EntityAddress> getAddresses()  {[
                addr
            ]}
            def List<EntityEmail> getEmailAddresses()  {[
                email
            ]}
            def List<EntityPhone> getPhoneNumbers()  {[
                phone
            ]}
            def EntityAddress getDefaultAddress() { addr }
            def EntityEmail getDefaultEmailAddress() { email }
            def EntityPhone getDefaultPhoneNumber() { phone }
            def boolean active = EntityTypeDataTest.ACTIVE.toBoolean()
            def Long versionNumber = EntityTypeDataTest.VERSION_NUMBER;
            def String objectId = EntityTypeDataTest.OBJECT_ID
        }).build()

	}
}
