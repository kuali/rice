/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.kuali.rice.shareddata.api.postalcode

import javax.xml.bind.JAXBContext
import org.junit.Assert
import org.junit.Test

class PostalCodeTest {
    private static final String CODE = "48848";
    private static final String CITY_NAME = "Laingsburg"
    private static final String COUNTRY_CODE = "US";
    private static final String STATE_CODE = "MI";
	private static final String COUNTY_CODE = "SHA"
	private static final String ACTIVE = "true";

    private static final String XML = """
    <postalCode xmlns="http://rice.kuali.org/shareddata/v1_1">
        <code>${CODE}</code>
        <cityName>${CITY_NAME}</cityName>
        <countryCode>${COUNTRY_CODE}</countryCode>
        <stateCode>${STATE_CODE}</stateCode>
        <countyCode>${COUNTY_CODE}</countyCode>
        <active>${ACTIVE}</active>
    </postalCode>
    """

    @Test
    void happy_path() {
        PostalCode.Builder.create(CODE, COUNTRY_CODE).build();
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
	  def jc = JAXBContext.newInstance(PostalCode.class)
	  def marshaller = jc.createMarshaller()
	  def sw = new StringWriter()

	  def param = this.create()
	  marshaller.marshal(param,sw)
      println(sw)
	  def unmarshaller = jc.createUnmarshaller();
	  def actual = unmarshaller.unmarshal(new StringReader(sw.toString()))
	  def expected = unmarshaller.unmarshal(new StringReader(XML))

	  Assert.assertEquals(expected,actual)
	}

    private create() {
		return PostalCode.Builder.create(new PostalCodeContract() {
			def String code = PostalCodeTest.CODE
            def String cityName = PostalCodeTest.CITY_NAME
            def String countryCode = PostalCodeTest.COUNTRY_CODE
            def String stateCode = PostalCodeTest.STATE_CODE
            def String countyCode = PostalCodeTest.COUNTY_CODE
            def boolean active = PostalCodeTest.ACTIVE.toBoolean()
        }).build()
	}
}
