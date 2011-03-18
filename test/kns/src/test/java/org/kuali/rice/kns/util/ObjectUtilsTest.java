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
package org.kuali.rice.kns.util;

import org.junit.Test;
import org.kuali.rice.core.impl.parameter.ParameterBo;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.test.KNSTestCase;

import static org.junit.Assert.assertFalse;

/**
 * ObjectUtilsTest
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ObjectUtilsTest extends KNSTestCase {
    @Test
    public void testObjectUtils_equalsByKey() throws Exception {
        ParameterBo parameterInDB = new ParameterBo();
        parameterInDB.setNamespaceCode("KR-NS");
        parameterInDB.setName("OBJ_UTIL_TEST");
        
        ParameterBo parameterNew = new ParameterBo();
        parameterNew.setNamespaceCode("KR-NS");
        parameterInDB.setName(null);
        
        boolean equalsResult = false;
        equalsResult = ObjectUtils.equalByKeys(parameterInDB, parameterNew);
        assertFalse(equalsResult);
    }
    
/*	@Test
	public void testGetFormatterWithDataDictionary() throws Exception {
		// test formatter getting correctly pulled from data dictionary
		TravelAccountUseRate useRate = new TravelAccountUseRate();
		Formatter formatter = ObjectUtils.getFormatterWithDataDictionary(useRate, "active");
		assertTrue("Incorrect formatter returned for active property", formatter instanceof BooleanFormatter);

		changeAttributeDefinitionFormatter(useRate.getClass(), "active", IntegerFormatter.class);
		formatter = ObjectUtils.getFormatterWithDataDictionary(useRate, "active");
		assertTrue("Incorrect formatter returned for active property", formatter instanceof IntegerFormatter);
		
		// test formatter getting correctly pulled by data type
		formatter = ObjectUtils.getFormatterWithDataDictionary(useRate, "activeFromDate");
		assertTrue("Incorrect formatter returned for date type", formatter instanceof DateFormatter);
		
		formatter = ObjectUtils.getFormatterWithDataDictionary(useRate, "rate");
		assertTrue("Incorrect formatter returned for percent type", formatter instanceof PercentageFormatter);
		
		formatter = ObjectUtils.getFormatterWithDataDictionary(useRate, "number");
		assertTrue("Incorrect formatter returned for string type", formatter.getClass().getName().equals("org.kuali.rice.core.web.format.Formatter"));
	}
*/
	private void changeAttributeDefinitionFormatter(Class boClass, String attributeName, Class formatterClass) {
		DataDictionaryEntryBase entry = (DataDictionaryEntryBase) KNSServiceLocatorWeb.getDataDictionaryService()
				.getDataDictionary().getDictionaryObjectEntry(boClass.getName());
		if (entry != null) {
			AttributeDefinition attributeDefinition = entry.getAttributeDefinition(attributeName);
			attributeDefinition.setFormatterClass(formatterClass.getName());
		}
	}
}
