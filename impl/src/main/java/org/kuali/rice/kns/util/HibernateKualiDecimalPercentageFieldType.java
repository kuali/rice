/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.util;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.service.KNSServiceLocator;
/**
 * This is a description of what this class does - g1zhang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class HibernateKualiDecimalPercentageFieldType extends HibernateKualiDecimalFieldType implements UserType{

	private static BigDecimal oneHundred = new BigDecimal(100.0000);

	/* Retrieves a value from the given ResultSet and decrypts it
	 * 
	 * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object source) throws HibernateException, SQLException {

		// Do conversion if our type is correct (BigDecimal).
		if (source != null && source instanceof BigDecimal) {
			BigDecimal converted = (BigDecimal) source;

			// Once we have converted, we need to do the super conversion to KualiDecimal.
			return super.nullSafeGet(rs, names, converted.multiply(oneHundred));
		}
		else {
			return null;
		}
	}

	/**
	 * sets the hash value on the PreparedStatement
	 * 
	 * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
	 */
	public void nullSafeSet(PreparedStatement st, Object value,  int index) throws HibernateException, SQLException {

		// Convert to BigDecimal using existing conversion.

		Object source = super.getConverted(value);

		BigDecimal converted = null;
		// Check for null, and verify object type.
		// Do conversion if our type is correct (BigDecimal).
		if (source != null && source instanceof BigDecimal) {
			converted = ((BigDecimal) source).divide(oneHundred, 4, KualiDecimal.ROUND_BEHAVIOR);
		}

		if (converted == null) {
			st.setNull(index, Types.DECIMAL);
		} else {
			st.setBigDecimal(index, converted);
		}

	}

	public Object getConvertedPercentage(Object source){
		
		if (source != null && source instanceof BigDecimal) {
			return ((BigDecimal) source).divide(oneHundred, 4, KualiDecimal.ROUND_BEHAVIOR);
		}
		else
			return null;
	}

	/**
	 * Returns String.class
	 * 
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	public Class returnedClass() {
		return BigDecimal.class;
	}

	/**
	 * Returns an array with the SQL VARCHAR type as the single member
	 * 
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return new int[] { Types.DECIMAL };
	}


}
