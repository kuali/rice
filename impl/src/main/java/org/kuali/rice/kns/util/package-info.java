@org.hibernate.annotations.TypeDefs ({
	@org.hibernate.annotations.TypeDef(
		name="rice_encrypt_decrypt",
		typeClass=org.kuali.rice.kns.util.HibernateKualiEncryptDecryptUserType.class
	),
	@org.hibernate.annotations.TypeDef(
		name="rice_active_inactive",
		typeClass=org.kuali.rice.kns.util.HibernateKualiCharBooleanAIType.class
	),
	@org.hibernate.annotations.TypeDef(
		name="rice_hash",
		typeClass=org.kuali.rice.kns.util.HibernateKualiHashType.class
	),
	@org.hibernate.annotations.TypeDef(
		name="rice_decimal",
		typeClass=org.kuali.rice.kns.util.HibernateKualiDecimalType.class
	),
	@org.hibernate.annotations.TypeDef(
		name="rice_percent",
		typeClass=org.kuali.rice.kns.util.HibernateKualiPercentType.class
	),
	@org.hibernate.annotations.TypeDef(
		name="rice_percentage",
		typeClass=org.kuali.rice.kns.util.HibernateKualiPercentageType.class
	),
	@org.hibernate.annotations.TypeDef(
		name="rice_integer",
		typeClass=org.kuali.rice.kns.util.HibernateKualiIntegerType.class
	),
	@org.hibernate.annotations.TypeDef(
		name="rice_integer_percentage",
		typeClass=org.kuali.rice.kns.util.HibernateKualiIntegerPercentageType.class
	)
})

package org.kuali.rice.kns.util;