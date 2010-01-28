@org.hibernate.annotations.TypeDefs ({
	@org.hibernate.annotations.TypeDef(
		name="rice_kns_encrypt_decrypt",
		typeClass=org.kuali.rice.kns.util.HibernateImmutableValueUserType.class
	)
})
package org.kuali.rice.kns.util;