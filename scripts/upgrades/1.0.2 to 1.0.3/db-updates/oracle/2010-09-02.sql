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

-- KULRICE-4517 - Needed for effective dating. This is a travel app table
CREATE TABLE TRV_ACCT_USE_RT_T ( 
    ID VARCHAR2(40) PRIMARY KEY, 
    ACCT_NUM VARCHAR2(10) NOT NULL, 
    RATE NUMBER(8) NOT NULL, 
    ACTV_FRM_DT DATE DEFAULT NULL, 
    ACTV_TO_DT DATE DEFAULT NULL
)
/
