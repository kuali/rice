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
package org.apache.ojb.broker.platforms;

/* Copyright 2002-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ojb.broker.query.LikeCriteria;

/**
 * @version 1.0
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PlatformMySQLImpl extends PlatformDefaultImpl
{
    private static final String LAST_INSERT = "SELECT LAST_INSERT_ID() FROM ";
    private static final String LIMIT = " LIMIT 1";
    
    /*
	 * @see Platform#setObjectForStatement(PreparedStatement, int, Object, int)
	 */
    public void setObjectForStatement(PreparedStatement ps, int index, Object value, int sqlType) throws SQLException
    {
        switch (sqlType)
        {
            case Types.BIT :
                ps.setObject(index, value);
                break;

            case Types.BLOB :
            case Types.LONGVARBINARY :
            case Types.VARBINARY :
                if (value instanceof byte[])
                {
                    byte buf[] = (byte[]) value;
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
                    ps.setBinaryStream(index, inputStream, buf.length);

                    break;
                }

            case Types.CLOB :
                Reader reader = null;
                int length = 0;

                if (value instanceof String)
                {
                    reader = new StringReader((String) value);
                    length = (((String) value)).length();
                }
                else if (value instanceof char[])
                {
                    String string = new String((char[])value);
                    reader = new StringReader(string);
                    length = string.length();
                }
                else if (value instanceof byte[])
                {
                    byte buf[] = (byte[]) value;
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
                    reader = new InputStreamReader(inputStream);
                }

                ps.setCharacterStream(index, reader, length);
                break;

            default :
                super.setObjectForStatement(ps, index, value, sqlType);

        }
    }
    /**
	 * Get join syntax type for this RDBMS - one on of the constants from
	 * JoinSyntaxType interface
	 */
    public byte getJoinSyntaxType()
    {
        return SQL92_NOPAREN_JOIN_SYNTAX;
    }

    public String getLastInsertIdentityQuery(String tableName)
    {
        return LAST_INSERT + tableName + LIMIT;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ojb.broker.platforms.Platform#addPagingSql(java.lang.StringBuffer)
	 */
    public void addPagingSql(StringBuffer anSqlString)
    {
        anSqlString.append(" LIMIT ?,?");
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.platforms.Platform#createSequenceQuery(String)
     */
    public String createSequenceQuery(String sequenceName)
    {
        return "insert into ojb_nextval_seq (seq_name) " + 
        		"values ('" + sequenceName + "')";
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.platforms.Platform#nextSequenceQuery(String)
     */
    public String nextSequenceQuery(String sequenceName)
    {
        return "select ojb_nextval_func ('" + sequenceName + "')";
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.platforms.Platform#dropSequenceQuery(String)
     */
    public String dropSequenceQuery(String sequenceName)
    {
        return "delete from ojb_nextval_seq where seq_name='" + 
        		sequenceName + "'";
    }
    
    /* (non-Javadoc)
     * Copied over from the OJB implementations for Informix
     */
    public CallableStatement prepareNextValProcedureStatement (Connection con,
    							String procedureName, String sequenceName) throws
    							PlatformException
    {
    	try {
    		String sp = " { call " + procedureName + " (?,?) } ";    		
    		CallableStatement cs = con.prepareCall(sp);
		cs.registerOutParameter(1, Types.BIGINT);
		cs.setString(2, sequenceName);
    		return cs;
    	} catch (Exception e) {
    		throw new PlatformException(e);
    	}
    }
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.ojb.broker.platforms.Platform#supportsPaging()
	 */
    public boolean supportsPaging()
    {
        return true;
    }

    /**
     * @see org.apache.ojb.broker.platforms.Platform#concatenate(java.lang.String[])
     */
    public String concatenate(String[] theColumns)
    {
        if (theColumns.length == 1)
        {
            return theColumns[0];
        }
        
        StringBuffer buf = new StringBuffer();
        
        buf.append("concat(");
        for (int i = 0; i < theColumns.length; i++)
        {
            if (i > 0)
            {
                buf.append(",");
            }
            buf.append(theColumns[i]);
        }

        buf.append(")");
        return buf.toString();
    }    
    
    /**
     * @see org.apache.ojb.broker.platforms.Platform#getEscapeClause(org.apache.ojb.broker.query.LikeCriteria)
     */
    public String getEscapeClause(LikeCriteria aCriteria)
    {
        if (LikeCriteria.getEscapeCharacter() != LikeCriteria.DEFAULT_ESCPAPE_CHARACTER)  
        {
            // the default escape character is \, so there's no need for an escape clause
            return super.getEscapeClause(aCriteria);
        }
        return "";
    }    
}
