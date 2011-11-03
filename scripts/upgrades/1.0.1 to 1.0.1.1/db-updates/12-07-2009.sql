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

-- KULRICE-3700 -- Add DataDictionarySearchableAttributeTest to the master databases and update validation params
UPDATE KRNS_PARM_T SET TXT='MM/dd/yyyy;MM/dd/yyyy HH:mm:ss;MM/dd/yy;MM-dd-yy;MMddyy;MMMM dd;yyyy;MM/dd/yy HH:mm:ss;MM-dd-yy HH:mm:ss;MMddyy HH:mm:ss;MMMM dd HH:mm:ss;yyyy HH:mm:ss' WHERE NMSPC_CD='KR-NS' AND PARM_DTL_TYP_CD='All' AND PARM_NM='STRING_TO_DATE_FORMATS' AND APPL_NMSPC_CD='KUALI' 
/ 
UPDATE KRNS_PARM_T SET TXT='MM/dd/yyyy hh:mm a;MM/dd/yyyy;MM/dd/yyyy HH:mm:ss;MM/dd/yy;MM-dd-yy;MMddyy;MMMM dd;yyyy;MM/dd/yy HH:mm:ss;MM-dd-yy HH:mm:ss;MMddyy HH:mm:ss;MMMM dd HH:mm:ss;yyyy HH:mm:ss' WHERE NMSPC_CD='KR-NS' AND PARM_DTL_TYP_CD='All' AND PARM_NM='STRING_TO_TIMESTAMP_FORMATS' AND APPL_NMSPC_CD='KUALI' 
/ 
CREATE TABLE ACCT_DD_ATTR_DOC ( 
DOC_HDR_ID VARCHAR2(14), 
OBJ_ID VARCHAR2(36), 
VER_NBR NUMBER(14), 
ACCT_NUM NUMBER(14) NOT NULL, 
ACCT_OWNR VARCHAR2(50) NOT NULL, 
ACCT_BAL NUMBER(16,2) NOT NULL, 
ACCT_OPN_DAT DATE NOT NULL, 
ACCT_STAT VARCHAR2(30) NOT NULL, 
ACCT_UPDATE_DT_TM TIMESTAMP, 
ACCT_AWAKE VARCHAR2(1), 
CONSTRAINT ACCT_DD_ATTR_DOC_PK PRIMARY KEY (DOC_HDR_ID) 
) 
/ 
