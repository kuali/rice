package org.kuali.rice.core.jpa.dialect;

import java.sql.Types;

public class MySQLDialect extends org.hibernate.dialect.MySQLDialect {
	
	public MySQLDialect() {
		super();
		registerColumnType( Types.BIT, "char(1)" );
	}

}
