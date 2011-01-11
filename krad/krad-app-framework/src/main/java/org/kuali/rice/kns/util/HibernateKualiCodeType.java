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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.kuali.rice.kns.bo.KualiCodeBase;

/**
  * This class crudely maps KualiCode to/from a String. It is intended as a temporary placeholder until a general technique for
 *   managing codes is decided upon.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class HibernateKualiCodeType extends HibernateImmutableValueUserType {

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.util.HibernateImmutableValueUserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
	 */
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)throws HibernateException, SQLException {
		
		String value = rs.getString(names[0]);
		String converted = null;
		
		if (value != null) {
			return new KualiCodeBase((String) value);
		}
		return value;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.util.HibernateImmutableValueUserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
	 */
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index)throws HibernateException, SQLException
	{
		String converted = null;
		if (value instanceof KualiCodeBase) {
			converted =  ((KualiCodeBase) value).getCode();
		}
		
        if (converted == null) {
        	st.setNull(index, Types.VARCHAR);
        } else {
        	st.setString(index, converted);
        }

	}
	
	/**
	 * Returns String.class
	 * 
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	public Class returnedClass() {
		return String.class;
	}

	/**
	 * Returns an array with the SQL VARCHAR type as the single member
	 * 
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}

}
