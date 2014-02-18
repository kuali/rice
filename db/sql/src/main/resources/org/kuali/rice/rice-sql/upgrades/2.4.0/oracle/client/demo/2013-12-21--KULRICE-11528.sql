--
-- Copyright 2005-2014 The Kuali Foundation
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

-- Adding back the missing travel account extension table
CREATE TABLE TRV_ACCT_EXT
(
      ACCT_NUM VARCHAR2(10)
        , ACCT_TYPE VARCHAR2(100)
        , OBJ_ID VARCHAR2(36)
        , VER_NBR DECIMAL(8,0) default 1 not null
)
/
ALTER TABLE TRV_ACCT_EXT
    ADD CONSTRAINT TRV_ACCT_EXTP1
PRIMARY KEY (ACCT_NUM,ACCT_TYPE)
/

