--
-- Copyright 2005-2012 The Kuali Foundation
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


--
--     KULRICE-6855: Travel Account Maintenance document routes to exception
--

alter table TRV_ACCT drop column ACCT_TYPE;

insert into KRMS_CNTXT_T
(CNTXT_ID, NMSPC_CD, NM, TYP_ID, ACTV, VER_NBR)
values ('trav-acct-test-ctxt','KR-SAP', 'Travel Account', 'T4', 'Y', 1);
