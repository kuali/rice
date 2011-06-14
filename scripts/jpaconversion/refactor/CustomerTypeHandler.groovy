def handleTypes(conversion, annotation, text){
	if (conversion.contains("OjbCharBooleanConversion")){
		annotation += "@Type(type=\"yes_no\")\n\t"
		text = addOtherImport(text, "org.hibernate.annotations.Type")
	} else if (conversion.contains("OjbCharBooleanFieldTFConversion")) {
		annotation += "@Type(type=\"true_false\")\n\t"
		text = addOtherImport(text, "org.hibernate.annotations.Type")
	} else if (conversion.contains("OjbCharBooleanFieldAIConversion")) {
		annotation += "@Type(type=\"rice_active_inactive\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.krad.util.HibernateKualiCharBooleanAIType")
	} else if (conversion.contains("OjbKualiHashFieldConversion")) {
		annotation += "@Type(type=\"rice_hash\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.krad.util.HibernateKualiHashType")
	} else if (conversion.contains("OjbKualiEncryptDecryptFieldConversion")) {
		annotation += "@Type(type=\"rice_encrypt_decrypt\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.krad.util.HibernateKualiEncryptDecryptUserType")
	} else if (conversion.contains("OjbKualiDecimalFieldConversion")) {
		annotation += "@Type(type=\"rice_decimal\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.krad.util.HibernateKualiDecimalFieldType")
	} else if (conversion.contains("OjbDecimalKualiPercentFieldConversion")) {
		annotation += "@Type(type=\"rice_decimal_percent\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.krad.util.HibernateKualiDecimalPercentFieldType")
	} else if (conversion.contains("OjbDecimalPercentageFieldConversion")) {
		annotation += "@Type(type=\"rice_decimal_percentage\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.krad.util.HibernateKualiDecimalPercentageFieldType")
	} else if (conversion.contains("OjbKualiIntegerFieldConversion")) {
		annotation += "@Type(type=\"rice_integer\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.krad.util.HibernateKualiIntegerFieldType")
	} else if (conversion.contains("OjbKualiPercentFieldConversion")) {
		annotation += "@Type(type=\"rice_integer_percent\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.krad.util.HibernateKualiIntegerPercentFieldType")
	} else if (conversion.contains("OjbKualiIntegerPercentageFieldConversion")) {
		annotation += "@Type(type=\"rice_integer_percentage\")\n\t"
		text = addOtherImport(text, "org.kuali.rice.krad.util.HibernateKualiIntegerPercentageFieldType")
	} else {
		println "UNHANDLED CONVERSION FOUND "+f.column
		println "conversion="+conversion
		//println "name="+f.name
	}
}

def addOtherImport(javaText, importText) {
	importText = "import ${importText};"
	if (!javaText.contains(importText)) {
		javaText = javaText.replaceFirst("package.*;", "\$0\n" + importText)
	}
	javaText 
}