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
package org.kuali.rice.core.api.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

import org.kuali.rice.core.api.util.jaxb.EnumStringAdapter;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * A enum that defines the type of an attribute definition.  Some of these enum values may not be supported in certain contexts.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum DataType {
	STRING(String.class,false,false),
	MARKUP(String.class,false,false),
	DATE(Date.class,false,true),
	TRUNCATED_DATE(Date.class,false,true),
    BOOLEAN(Boolean.class,false,false),
    INTEGER(Integer.class,true,false),
    FLOAT(Float.class,true,false),
    DOUBLE(Double.class,true,false),
    LONG(Long.class,true,false),
    DATETIME(Date.class,false,true),
    TIMESTAMP(Timestamp.class,false,true),
    CURRENCY(KualiDecimal.class,true,false)
    , PRECISE_DECIMAL(BigDecimal.class,true,false)
    , LARGE_INTEGER(BigInteger.class,true,false)
    ;

	private final Class<?> type;
	private final boolean numeric;
	private final boolean temporal;

	private DataType(Class<?> type, boolean numeric, boolean temporal) {
		this.type = type;
		this.numeric = numeric;
		this.temporal = temporal;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean isNumeric() {
	    return numeric;
	}

	public boolean isTemporal() {
	    return temporal;
	}

    public static final class Adapter extends EnumStringAdapter<DataType> {

        @Override
		protected Class<DataType> getEnumClass() {
			return DataType.class;
		}
	}

	/**
	 * Get the first matching datatype based on the passed in class.
	 *
	 * @param clazz
	 * @return Matching DataType if found, null otherwise.
	 */
	public static DataType getDataTypeFromClass(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		for (DataType dataType : DataType.values()) {
			if (dataType.type.isAssignableFrom(clazz)) {
				return dataType;
			}
		}
		return null;
	}
}
