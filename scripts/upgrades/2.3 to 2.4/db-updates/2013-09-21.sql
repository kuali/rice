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

-- KULRICE-10534 : Test data for Multi value select server side paging

INSERT INTO TRV_ACCT(ACCT_NUM, ACCT_NAME, ACCT_TYPE, ACCT_FO_ID, OBJ_ID, CREATE_DT, SUBSIDIZED_PCT)
  VALUES('a4', 'Travel Account 4', 'IAT', 'erin', 'a4', NULL, 45)
/
INSERT INTO TRV_ACCT(ACCT_NUM, ACCT_NAME, ACCT_TYPE, ACCT_FO_ID, OBJ_ID, CREATE_DT, SUBSIDIZED_PCT)
  VALUES('a5', 'Travel Account 5', 'IAT', 'eric', 'a5', NULL, 45)
/
INSERT INTO TRV_ACCT(ACCT_NUM, ACCT_NAME, ACCT_TYPE, ACCT_FO_ID, OBJ_ID, CREATE_DT, SUBSIDIZED_PCT)
  VALUES('a7', 'Travel Account 7', 'CAT', 'erin', 'a7', NULL, 75)
/
INSERT INTO TRV_ACCT(ACCT_NUM, ACCT_NAME, ACCT_TYPE, ACCT_FO_ID, OBJ_ID, CREATE_DT, SUBSIDIZED_PCT)
  VALUES('a10', 'Travel Account 10', 'CAT', 'eric', 'a10', NULL, 75)
/
INSERT INTO TRV_ACCT(ACCT_NUM, ACCT_NAME, ACCT_TYPE, ACCT_FO_ID, OBJ_ID, CREATE_DT, SUBSIDIZED_PCT)
  VALUES('a11', 'Travel Account 11', 'EAT', 'erin', 'a11', NULL, 85)
/
INSERT INTO TRV_ACCT(ACCT_NUM, ACCT_NAME, ACCT_TYPE, ACCT_FO_ID, OBJ_ID, CREATE_DT, SUBSIDIZED_PCT)
  VALUES('a12', 'Travel Account 12', 'EAT', 'eric', 'a12', NULL, 85)
/
INSERT INTO TRV_ACCT(ACCT_NUM, ACCT_NAME, ACCT_TYPE, ACCT_FO_ID, OBJ_ID, CREATE_DT, SUBSIDIZED_PCT)
  VALUES('a13', 'Travel Account 13', 'CAT', 'fran', 'a13', NULL, 95)
/
