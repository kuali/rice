--
-- Copyright 2005-2017 The Kuali Foundation
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

ALTER TABLE trv_acct ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE trv_acct ADD (VER_NBR NUMBER(8) DEFAULT 0)
/

ALTER TABLE trv_acct_type ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE trv_acct_type ADD (VER_NBR NUMBER(8) DEFAULT 0)
/

ALTER TABLE trv_acct_fo ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE trv_acct_fo ADD (VER_NBR NUMBER(8) DEFAULT 0)
/

ALTER TABLE trv_acct_ext ADD (OBJ_ID VARCHAR2(36))
/
ALTER TABLE trv_acct_ext ADD (VER_NBR NUMBER(8) DEFAULT 0)
/