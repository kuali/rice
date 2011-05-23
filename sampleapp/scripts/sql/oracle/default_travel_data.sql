--
-- Copyright 2010 The Kuali Foundation
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
-- DO NOT add comments before the blank line below, or they will disappear.
TRUNCATE TABLE TRV_ACCT_FO DROP STORAGE
/
INSERT INTO TRV_ACCT_FO (ACCT_FO_ID,ACCT_FO_USER_NAME)
  VALUES (1,'fred')
/
INSERT INTO TRV_ACCT_FO (ACCT_FO_ID,ACCT_FO_USER_NAME)
  VALUES (2,'fran')
/
INSERT INTO TRV_ACCT_FO (ACCT_FO_ID,ACCT_FO_USER_NAME)
  VALUES (3,'frank')
/
TRUNCATE TABLE TRV_ACCT DROP STORAGE
/
INSERT INTO TRV_ACCT (ACCT_FO_ID,ACCT_NAME,ACCT_NUM)
  VALUES (1,'a1','a1')
/
INSERT INTO TRV_ACCT (ACCT_FO_ID,ACCT_NAME,ACCT_NUM)
  VALUES (2,'a14','a14')
/
INSERT INTO TRV_ACCT (ACCT_FO_ID,ACCT_NAME,ACCT_NUM)
  VALUES (2,'a2','a2')
/
INSERT INTO TRV_ACCT (ACCT_FO_ID,ACCT_NAME,ACCT_NUM)
  VALUES (3,'a3','a3')
/
INSERT INTO TRV_ACCT (ACCT_FO_ID,ACCT_NAME,ACCT_NUM)
  VALUES (2,'a6','a6')
/
INSERT INTO TRV_ACCT (ACCT_FO_ID,ACCT_NAME,ACCT_NUM)
  VALUES (2,'a8','a8')
/
INSERT INTO TRV_ACCT (ACCT_FO_ID,ACCT_NAME,ACCT_NUM)
  VALUES (2,'a9','a9')
/

TRUNCATE TABLE TRV_ACCT_EXT DROP STORAGE
/
INSERT INTO TRV_ACCT_EXT (ACCT_NUM,ACCT_TYPE)
  VALUES ('a1','IAT')
/
INSERT INTO TRV_ACCT_EXT (ACCT_NUM,ACCT_TYPE)
  VALUES ('a14','CAT')
/
INSERT INTO TRV_ACCT_EXT (ACCT_NUM,ACCT_TYPE)
  VALUES ('a2','EAT')
/
INSERT INTO TRV_ACCT_EXT (ACCT_NUM,ACCT_TYPE)
  VALUES ('a3','IAT')
/
INSERT INTO TRV_ACCT_EXT (ACCT_NUM,ACCT_TYPE)
  VALUES ('a6','CAT')
/
INSERT INTO TRV_ACCT_EXT (ACCT_NUM,ACCT_TYPE)
  VALUES ('a8','EAT')
/
INSERT INTO TRV_ACCT_EXT (ACCT_NUM,ACCT_TYPE)
  VALUES ('a9','CAT')
/
TRUNCATE TABLE TRV_ACCT_TYPE DROP STORAGE
/
INSERT INTO TRV_ACCT_TYPE (ACCT_TYPE,ACCT_TYPE_NAME)
  VALUES ('CAT','Clearing Account Type')
/
INSERT INTO TRV_ACCT_TYPE (ACCT_TYPE,ACCT_TYPE_NAME)
  VALUES ('EAT','Expense Account Type')
/
INSERT INTO TRV_ACCT_TYPE (ACCT_TYPE,ACCT_TYPE_NAME)
  VALUES ('IAT',' Income Account Type')
/
