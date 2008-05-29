package org.kuali.rice.jpa.dialect;

import java.sql.Types;

public class MySQLDialect extends org.hibernate.dialect.MySQLDialect {
	
	public MySQLDialect() {
		super();
		registerColumnType( Types.BIT, "char(1)" );
	}

}
