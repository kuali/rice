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
/* Created on Sep 13, 2005 */
package org.apache.ojb.broker.platforms;

/* Copyright 2002-2004 The Apache Software Foundation
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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ojb.broker.query.LikeCriteria;

/**
 * Platform implementation for the Mckoi database.
 * 
 * @version 1.0
 * @author <a href="mailto:tomdz@apache.org">Thomas Dudziak</a>
 * @version $Id: PlatformMckoiImpl.java,v 1.3 2007-08-15 15:49:57 ewestfal Exp $
 * @see http://www.mckoi.com/database
 */
public class PlatformMckoiImpl extends PlatformDefaultImpl
{
    /* (non-Javadoc)
     * @see Platform#setObjectForStatement(PreparedStatement, int, Object, int)
     */
    public void setObjectForStatement(PreparedStatement statement, int index, Object value, int sqlType) throws SQLException
    {
        switch (sqlType)
        {
            case Types.BLOB :
            case Types.LONGVARBINARY :
            case Types.VARBINARY :
                if (value instanceof byte[])
                {
                    byte[]               buf         = (byte[])value;
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
                    statement.setBinaryStream(index, inputStream, buf.length);

                    break;
                }

            case Types.CLOB :
                Reader reader = null;
                int    length = 0;

                if (value instanceof String)
                {
                    reader = new StringReader((String)value);
                    length = (((String)value)).length();
                }
                else if (value instanceof char[])
                {
                    String string = new String((char[])value);

                    reader = new StringReader(string);
                    length = string.length();
                }
                else if (value instanceof byte[])
                {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream((byte[])value);

                    reader = new InputStreamReader(inputStream);
                }
                statement.setCharacterStream(index, reader, length);
                break;

            default :
                super.setObjectForStatement(statement, index, value, sqlType);

        }
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.platforms.Platform#createSequenceQuery(String)
     */
    public String createSequenceQuery(String sequenceName)
    {
        return "create sequence " + sequenceName;
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.platforms.Platform#nextSequenceQuery(String)
     */
    public String nextSequenceQuery(String sequenceName)
    {
        return "select nextval('" + sequenceName + "')";
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.platforms.Platform#dropSequenceQuery(String)
     */
    public String dropSequenceQuery(String sequenceName)
    {
        return "drop sequence " + sequenceName;
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.platforms.Platform#getJoinSyntaxType()
     */
    public byte getJoinSyntaxType()
    {
        return SQL92_NOPAREN_JOIN_SYNTAX;
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.platforms.Platform#supportsPaging()
     */
    public boolean supportsPaging()
    {
        // [tomdz] there is no explicit paging support a la LIMIT in Mckoi (yet ?)
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.platforms.Platform#concatenate(java.lang.String[])
     */
    public String concatenate(String[] columns)
    {
        if (columns.length == 1)
        {
            return columns[0];
        }
        
        StringBuffer buf = new StringBuffer();
        
        buf.append("concat(");
        for (int idx = 0; idx < columns.length; idx++)
        {
            if (idx > 0)
            {
                buf.append(",");
            }
            buf.append(columns[idx]);
        }
        buf.append(")");

        return buf.toString();
    }    
    
    /* (non-Javadoc)
     * @see org.apache.ojb.broker.platforms.Platform#getEscapeClause(org.apache.ojb.broker.query.LikeCriteria)
     */
    public String getEscapeClause(LikeCriteria criteria)
    {
        // [tomdz] Mckoi does not support escape characters other than \
        // TODO    Shold we throw some kind of exception here if the escape character is different ?
        return "";
    }    
}
