@org.hibernate.annotations.TypeDefs ({
	@org.hibernate.annotations.TypeDef(
		name="rice_kns_encrypt_decrypt",
		typeClass=org.kuali.rice.kns.util.HibernateImmutableValueUserType.class
	),
	@org.hibernate.annotations.TypeDef(
		name="active_inactive",
		typeClass=org.kuali.rice.kns.util.HibernateKualiCharBooleanAIType.class
	),
	@org.hibernate.annotations.TypeDef(
		name="rice_hash",
		typeClass=org.kuali.rice.kns.util.HibernateKualiHashType.class
	)
})

package org.kuali.rice.kns.util;