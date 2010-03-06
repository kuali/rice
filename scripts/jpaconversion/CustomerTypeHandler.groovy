def handleTypes(conversion, annotation, text, fields){
	if (conversion.contains("OjbCharBooleanConversion")){
		annotation += "@Type(type=\"yes_no\")\n\t"
		text = addOtherImport(text, "org.hibernate.annotations.Type")
	} else if (conversion.contains("OjbCharBooleanFieldTFConversion")) {
		annotation += "@Type(type=\"true_false\")\n\t"
		text = addOtherImport(text, "org.hibernate.annotations.Type")
	} else if (conversion.contains("OjbCharBooleanFieldAIConversion")) {
		annotation += "@Type(type=\"rice_active_inactive\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.kns.util.HibernateKualiCharBooleanAIType")
	} else if (conversion.contains("OjbKualiHashFieldConversion")) {
		annotation += "@Type(type=\"rice_hash\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.kns.util.HibernateKualiHashType")
	} else if (conversion.contains("OjbKualiEncryptDecryptFieldConversion")) {
		annotation += "@Type(type=\"rice_encrypt_decrypt\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.kns.util.HibernateKualiEncryptDecryptUserType")
	} else if (conversion.contains("OjbKualiDecimalFieldConversion")) {
		annotation += "@Type(type=\"rice_decimal\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.kns.util.HibernateKualiDecimalFieldType")
	} else if (conversion.contains("OjbDecimalKualiPercentFieldConversion")) {
		annotation += "@Type(type=\"rice_decimal_percent\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.kns.util.HibernateKualiDecimalPercentFieldType")
	} else if (conversion.contains("OjbDecimalPercentageFieldConversion")) {
		annotation += "@Type(type=\"rice_decimal_percentage\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.kns.util.HibernateKualiDecimalPercentageFieldType")
	} else if (conversion.contains("OjbKualiIntegerFieldConversion")) {
		annotation += "@Type(type=\"rice_integer\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.kns.util.HibernateKualiIntegerFieldType")
	} else if (conversion.contains("OjbKualiPercentFieldConversion")) {
		annotation += "@Type(type=\"rice_integer_percent\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.kns.util.HibernateKualiIntegerPercentFieldType")
	} else if (conversion.contains("OjbKualiIntegerPercentageFieldConversion")) {
		annotation += "@Type(type=\"rice_integer_percentage\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.kns.util.HibernateKualiIntegerPercentageFieldType")
	} 
	//for KFS
	else if (conversion.contains("OjbAccountActiveIndicatorConversion")){
		annotation += "@Type(type=\"yes_no\")\n\t"
		text = addOtherImport(text, "org.hibernate.annotations.Type")
	} 
	else if (conversion.contains("OjbCharBooleanFieldInverseConversion")){
		annotation += "@Type(type=\"kfs_inverse_boolean\")\n\t"
		text = addOtherImport(text, "org.kuali.kfs.sys.dataaccess.HibernateKFSCharBoolenFieldInverseType")
	} 
	else if (conversion.contains("OjbBCPositionActiveIndicatorConversion")){
		annotation += "@Type(type=\"kfs_bc_activeindicator\")\n\t"
		text = addOtherImport(text, "org.kuali.kfs.module.bc.util.HibernateKFSBCPositionActiveIndicatiorType")
	} 
	else if (conversion.contains("OjbBudgetConstructionFTEConversion")){
		annotation += "@Type(type=\"kra_bc_fte\")\n\t"
		text = addOtherImport(text, "org.kuali.kfs.module.bc.util.HibernateKFSBudgetConstructionFTEType")
	} 
	else if (conversion.contains("OjbBudgetConstructionPercentTimeConversion")){
		annotation += "@Type(type=\"kra_bc_percenttime\")\n\t"
		text = addOtherImport(text, "org.kuali.kfs.module.bc.util.HibernateKFSConstructionPercentTimeType")
	} 
	else if (conversion.contains("OjbPendingBCAppointmentFundingActiveIndicatorConversion")){
		annotation += "@Type(type=\"kra_bc_pendingfund_activeindictor\")\n\t"
		text = addOtherImport(text, "org.kuali.kfs.module.bc.util.HibernateKFSPendingBCAppointmentFundingActiveIndictorType")
	} 
	
	//for KRA
	else if (conversion.contains("UnitContactTypeConverter")){
		annotation += "@Type(type=\"kra_unit_contact\")\n\t"
		text = addOtherImport(text, "org.kuali.kra.award.contacts.HibernateKRAUnitContatcTypeType")
	} 
	else if (conversion.contains("OjbBudgetDecimalFieldConversion")){
		annotation += "@Type(type=\"kra_decimal\")\n\t"
		text = addOtherImport(text, "org.kuali.kra.infrastructure.HibernateKRADecimaFiedType")
	} 
	else if (conversion.contains("OjbOnOffCampusFlagFieldConversion")){
		annotation += "@Type(type=\"kra_campus_flag\")\n\t"
		text = addOtherImport(text, "org.kuali.kra.infrastructure.HibernateKRAOnOffCampusFlagFieldType")
	} 
	else if (conversion.contains("OjbRateDecimalFieldConversion")){
		annotation += "@Type(type=\"kra_rate_decimal\")\n\t"
		text = addOtherImport(text, "org.kuali.kra.infrastructure.HibernateKRADecimaFiedType")
	} 

	else {
		println "UNHANDLED CONVERSION FOUND "+fields.column
		println "conversion="+conversion
		println "name="+fields.name
	}
}

def addOtherImport(javaText, importText) {
	importText = "import ${importText};"
	if (!javaText.contains(importText)) {
		javaText = javaText.replaceFirst("package.*;", "\$0\n" + importText)
	}
	javaText 
}