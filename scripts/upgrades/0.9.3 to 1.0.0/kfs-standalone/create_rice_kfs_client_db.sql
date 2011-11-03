--
-- Copyright 2005-2011 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

CREATE DATABASE LINK rice_cli CONNECT TO rice094client IDENTIFIED BY client174rice094 USING 'KUALI'
/
-- Disable constraints for the duration of this script
DECLARE 
   CURSOR constraint_cursor IS 
      SELECT table_name, constraint_name 
         FROM user_constraints 
         WHERE constraint_type = 'R'
           AND status = 'ENABLED';
BEGIN 
   FOR r IN constraint_cursor LOOP
      execute immediate 'ALTER TABLE '||r.table_name||' DISABLE CONSTRAINT '||r.constraint_name; 
   END LOOP; 
END;
/
-- empty ALL the tables
DECLARE
     CURSOR rice_tables IS 
          SELECT object_name AS table_name
               FROM user_objects 
               WHERE object_type IN ( 'TABLE' )
                 AND OBJECT_NAME NOT LIKE 'TR%'; 
     CURSOR tbl_cols ( TableName IN VARCHAR2 ) IS
        SELECT column_name
            FROM user_tab_cols
            WHERE table_name = TableName;
     InsertSQL VARCHAR2(4000);
     FirstCol BOOLEAN;
BEGIN
    -- disable rice table constraints
    FOR tbl IN rice_tables LOOP
        -- detect if the table is in the client database
        BEGIN
            EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM '||tbl.table_name||'@rice_cli';
            dbms_output.put_line( 'Skipping - Rice Client Table: '||tbl.table_name );
            GOTO skip;
        EXCEPTION
            WHEN others THEN
                NULL;
        END;
        BEGIN
            -- attempt to detect the table on the other side and abort if not found
            EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM '||tbl.table_name||'@kfs';
            dbms_output.put_line( 'Purging and loading table:'||tbl.table_name );
            EXECUTE IMMEDIATE 'TRUNCATE TABLE '||tbl.table_name;
            -- empty the tables        
            -- pull all the data from the KFS tables
            InsertSQL := 'INSERT INTO '||tbl.table_name||' (';
            FirstCol := TRUE;
            FOR col IN tbl_cols ( tbl.table_name ) LOOP
                IF FirstCol THEN
                    FirstCol := FALSE;
                ELSE
                    InsertSQL := InsertSQL||',';
                END IF;
                InsertSQL := InsertSQL||col.column_name;
            END LOOP;
            InsertSQL := InsertSQL||') (SELECT ';
            FirstCol := TRUE;
            FOR col IN tbl_cols ( tbl.table_name ) LOOP
                IF FirstCol THEN
                    FirstCol := FALSE;
                ELSE
                    InsertSQL := InsertSQL||',';
                END IF;
                InsertSQL := InsertSQL||col.column_name;
            END LOOP;
            InsertSQL := InsertSQL||' FROM '||tbl.table_name||'@kfs)';
            --dbms_output.put_line( InsertSQL );
            BEGIN
                EXECUTE IMMEDIATE InsertSQL;
            EXCEPTION
                WHEN others THEN
                    IF SQLERRM LIKE '%ORA-00942%' THEN
                        NULL;
                    ELSE
                        dbms_output.put_line( 'Failure loading table:'||tbl.table_name );
                        dbms_output.put_line( SQLERRM );
                    END IF;
            END;
            COMMIT;
        EXCEPTION
            WHEN OTHERS then
                dbms_output.put_line( 'Skipping - NOT IN KFS: '||tbl.table_name );
        END;
        <<skip>>
        NULL;
    END LOOP;
END;
/
-- now, drop the tables in the schema imported from kfs-cfg-dbs/trunk
-- build the script from the current rice schema
DECLARE
     CURSOR rice_tables IS 
          SELECT object_name AS table_name
               FROM user_objects 
               WHERE object_type IN ( 'TABLE' )
                 AND OBJECT_NAME NOT LIKE 'TR%'; 
     CURSOR tbl_cols ( TableName IN VARCHAR2 ) IS
        SELECT column_name
            FROM user_tab_cols
            WHERE table_name = TableName;
     InsertSQL VARCHAR2(4000);
     FirstCol BOOLEAN;
BEGIN
    -- disable rice table constraints
    FOR tbl IN rice_tables LOOP
        -- detect if the table is in the client database
        BEGIN
            EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM '||tbl.table_name||'@rice_cli';
            GOTO skip;
        EXCEPTION
            WHEN others THEN
                NULL;
        END;
        BEGIN
            -- attempt to detect the table on the other side and abort if not found
            EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM '||tbl.table_name||'@kfs';
            dbms_output.put_line( 'DROP TABLE '||tbl.table_name );
            dbms_output.put_line( '/' );
        EXCEPTION
            WHEN OTHERS then
                NULL;
        END;
        <<skip>>
        NULL;
    END LOOP;
END;
/
--  Re-enable constraints */
DECLARE 
   CURSOR constraint_cursor IS 
      SELECT table_name, constraint_name 
         FROM user_constraints 
         WHERE constraint_type = 'R'
           AND status <> 'ENABLED';
BEGIN 
   FOR r IN constraint_cursor LOOP
      execute immediate 'ALTER TABLE '||r.table_name||' ENABLE CONSTRAINT '||r.constraint_name; 
   END LOOP; 
END;
/
