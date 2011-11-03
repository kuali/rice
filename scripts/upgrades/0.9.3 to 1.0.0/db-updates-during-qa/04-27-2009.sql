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

CREATE SEQUENCE KRNS_MAINT_LOCK_S START WITH 2000 INCREMENT BY 1
/
ALTER TABLE KRNS_MAINT_LOCK_T ADD MAINT_LOCK_ID VARCHAR2(14)
/
ALTER TABLE KRNS_MAINT_LOCK_T DROP PRIMARY KEY
/

DECLARE
CURSOR cursor1 IS
	SELECT MAINT_LOCK_REP_TXT FROM KRNS_MAINT_LOCK_T;
BEGIN
	FOR r IN cursor1 LOOP
        execute immediate 'UPDATE KRNS_MAINT_LOCK_T SET MAINT_LOCK_ID=KRNS_MAINT_LOCK_S.nextval';
    END LOOP;
END;
/

ALTER TABLE KRNS_MAINT_LOCK_T ADD PRIMARY KEY (MAINT_LOCK_ID)
/

ALTER TABLE KRNS_MAINT_LOCK_T MODIFY MAINT_LOCK_REP_TXT VARCHAR2(500)
/

