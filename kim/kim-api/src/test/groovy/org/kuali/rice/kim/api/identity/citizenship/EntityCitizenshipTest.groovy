package org.kuali.rice.kim.api.identity.citizenship

import java.sql.Timestamp
import java.text.SimpleDateFormat
import org.junit.Test
import org.kuali.rice.kim.api.identity.Type
import org.kuali.rice.kim.api.identity.TypeContract
import org.kuali.rice.kim.api.test.JAXBAssert
import org.junit.Assert


class EntityCitizenshipTest {
    private static final String ID = "1";
	private static final String ENTITY_ID = "190192";
    private static final String STATUS_CODE = "Home"
    private static final String STATUS_NAME = "Home-y"
    private static final String STATUS_SORT_CODE = "0"
    private static final String STATUS_ACTIVE = "true"
    private static final Long STATUS_VERSION_NUMBER = new Integer(1)
	private static final String STATUS_OBJECT_ID = UUID.randomUUID()
    private static final String COUNTRY_CODE = "MX";
    static final String START_DATE_STRING = "2011-01-01 12:00:00.0"
    static final Timestamp START_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(START_DATE_STRING).toTimestamp()
    static final String END_DATE_STRING = "2012-01-01 12:00:00.0"
    static final Timestamp END_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(END_DATE_STRING).toTimestamp()
    private static final String ACTIVE = "true"
    private static final Long VERSION_NUMBER = new Integer(1);
	private static final String OBJECT_ID = UUID.randomUUID();

    private static final String XML = """
    <entityCitizenship xmlns="http://rice.kuali.org/kim/v2_0">
        <id>${ID}</id>
        <entityId>${ENTITY_ID}</entityId>
        <status>
            <code>${STATUS_CODE}</code>
            <name>${STATUS_NAME}</name>
            <active>${STATUS_ACTIVE}</active>
            <sortCode>${STATUS_SORT_CODE}</sortCode>
            <versionNumber>${STATUS_VERSION_NUMBER}</versionNumber>
            <objectId>${STATUS_OBJECT_ID}</objectId>
        </status>
        <countryCode>${COUNTRY_CODE}</countryCode>
        <startDate>${START_DATE.time}</startDate>
        <endDate>${END_DATE.time}</endDate>
        <active>${ACTIVE}</active>
        <versionNumber>${VERSION_NUMBER}</versionNumber>
        <objectId>${OBJECT_ID}</objectId>
    </entityCitizenship>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_id_whitespace() {
        EntityCitizenship.Builder builder = EntityCitizenship.Builder.create();
        builder.setId(" ")
    }

    @Test
    void test_copy() {
        def o1 = EntityCitizenship.Builder.create().build();
        def o2 = EntityCitizenship.Builder.create(o1).build();

        Assert.assertEquals(o1, o2);
    }

    @Test
    void happy_path() {
        EntityCitizenship.Builder.create();
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, EntityCitizenship.class)
	}

    private create() {
		return EntityCitizenship.Builder.create(new EntityCitizenshipContract() {
			def String id = EntityCitizenshipTest.ID
            def String entityId = EntityCitizenshipTest.ENTITY_ID
			def Type getStatus() { Type.Builder.create(new TypeContract() {
				def String code = EntityCitizenshipTest.STATUS_CODE
				def String name = EntityCitizenshipTest.STATUS_NAME
				def boolean active = EntityCitizenshipTest.STATUS_ACTIVE
                def String sortCode = EntityCitizenshipTest.STATUS_SORT_CODE
                def Long versionNumber = EntityCitizenshipTest.STATUS_VERSION_NUMBER
				def String objectId = EntityCitizenshipTest.STATUS_OBJECT_ID
			}).build()
            }
            def String countryCode = EntityCitizenshipTest.COUNTRY_CODE
            def Timestamp startDate = EntityCitizenshipTest.START_DATE
            def Timestamp endDate = EntityCitizenshipTest.END_DATE
            def boolean active = EntityCitizenshipTest.ACTIVE.toBoolean()
            def Long versionNumber = EntityCitizenshipTest.VERSION_NUMBER;
			def String objectId = EntityCitizenshipTest.OBJECT_ID
        }).build()

	}
}
