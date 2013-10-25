import ojbmetadata.Field;


/**
 * Copyright 2005-2013 The Kuali Foundation
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
def handleCustomTypes( String conversion, Field field){
	def converterClass = ''
	/* RICE CONVERTERS IN USE: 10/25/2013
	 *  conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion"
		conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion2"
		conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion3"
		conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversionTF"
		conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiDecimalFieldConversion"
		conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiEncryptDecryptFieldConversion"
		conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiHashFieldConversion"
		conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiIntegerFieldConversion"
		conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiPercentFieldConversion"
		conversion="org.kuali.rice.krms.ojb.OjbRelationshipTypeStringConversion"
	 */
	try{
		//println 'ojb converter: ' + conversion + ' converterClass: ' + converterClass
		if (conversion.contains("OjbCharBooleanConversion")){
			// DO NOTHING - Boolean conversions are handled by default
		} else if (conversion.contains("OjbCharBooleanConversionTF")) {
			converterClass += "BooleanTFConverter"
		} else if (conversion.contains("OjbKualiDecimalFieldConversion")) {
			// DO NOTHING - conversions are handled by default
		} else if (conversion.contains("OjbKualiEncryptDecryptFieldConversion")) {
			converterClass += "EncryptionConverter"
		} else if (conversion.contains("OjbKualiHashFieldConversion")) {
			converterClass += "HashConverter"
		} else if (conversion.contains("OjbKualiIntegerFieldConversion")) {
			// DO NOTHING - conversions are handled by default
		} else if (conversion.contains("OjbKualiPercentFieldConversion")) {
			// DO NOTHING - conversions are handled by default
		} 
//		} else if (conversion.contains("OjbDecimalKualiPercentFieldConversion")) {
//			converterClass += "@Type(type=\"rice_decimal_percent\")\n\t"
//		} else if (conversion.contains("OjbDecimalPercentageFieldConversion")) {
//			converterClass += "@Type(type=\"rice_decimal_percentage\")\n\t"
//		} else if (conversion.contains("OjbKualiIntegerPercentageFieldConversion")) {
//			converterClass += "@Type(type=\"rice_integer_percentage\")\n\t"
		
		
		/* KFS Converters in Use 5.0.3
		 * conversion="org.kuali.kfs.coa.util.OjbAccountActiveIndicatorConversion" />
			conversion="org.kuali.kfs.module.bc.util.OjbBCPositionActiveIndicatorConversion" />
			conversion="org.kuali.kfs.module.bc.util.OjbBudgetConstructionFTEConversion"/>
			conversion="org.kuali.kfs.module.bc.util.OjbBudgetConstructionPercentTimeConversion"/>
			conversion="org.kuali.kfs.module.bc.util.OjbPendingBCAppointmentFundingActiveIndicatorConversion"/>
			conversion="org.kuali.kfs.sys.dataaccess.OjbCharBooleanFieldAIConversion" />
			conversion="org.kuali.kfs.sys.dataaccess.OjbCharBooleanFieldInverseConversion" />
			conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion" />
			conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiDecimalFieldConversion" />
			conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiEncryptDecryptFieldConversion" />
			conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiIntegerFieldConversion" />
			conversion="org.kuali.rice.core.framework.persistence.ojb.conversion.OjbKualiPercentFieldConversion" />
			conversion="org.kuali.rice.kns.util.OjbCharBooleanFieldAIConversion"/-->
		 */
		//for KFS
		else if (conversion.contains("OjbAccountActiveIndicatorConversion") 
			|| conversion.contains("OjbAccountActiveIndicatorConversion")
			|| conversion.contains("OjbCharBooleanFieldInverseConversion")
			|| conversion.contains("OjbPendingBCAppointmentFundingActiveIndicatorConversion") ){
			converterClass += "InverseBooleanYNConverter"
		} 
		else if (conversion.contains("OjbBCPositionActiveIndicatorConversion")
			|| conversion.contains("OjbCharBooleanFieldAIConversion")){
			converterClass += "BooleanAIConverter"
		} 
//		else if (conversion.contains("OjbBudgetConstructionFTEConversion")){
//			converterClass += "@Type(type=\"kra_bc_fte\")\n\t"
//		} 
//		else if (conversion.contains("OjbBudgetConstructionPercentTimeConversion")){
//			converterClass += "@Type(type=\"kra_bc_percenttime\")\n\t"
//		} 
		
		//for KRA
//		else if (conversion.contains("UnitContactTypeConverter")){
//			converterClass += "@Type(type=\"kra_unit_contact\")\n\t"
//		} 
//		else if (conversion.contains("OjbBudgetDecimalFieldConversion")){
//			converterClass += "@Type(type=\"kra_decimal\")\n\t"
//		} 
//		else if (conversion.contains("OjbOnOffCampusFlagFieldConversion")){
//			converterClass += "@Type(type=\"kra_campus_flag\")\n\t"
//		} 
//		else if (conversion.contains("OjbRateDecimalFieldConversion")){
//			converterClass += "@Type(type=\"kra_rate_decimal\")\n\t"
//		} 
		else {
			println "UNHANDLED CONVERSION FOUND "+field.column
			println "conversion="+conversion
			println "name="+field.name
			converterClass += "/*Was OJB: $conversion*/UnknownConverter"
		}
	} catch(Exception e) { 
		println "ERROR HANDLING TYPE CONVERSION "+field.column
		println "conversion="+conversion
		println "name="+field.name
		println( e.getClass().getName() + " : " +  e.getMessage());
		converterClass += "/*Was OJB: $conversion*/ExceptionDuringConversion"
	}
	
	return converterClass
}

