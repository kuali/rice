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

alter table "EN_DOC_HDR_T" drop column "DOC_OVRD_IND"
/
alter table "EN_DOC_HDR_T" drop column "DOC_LOCK_CD"
/
alter table "EN_RTE_NODE_T" drop column "CONTENT_FRAGMENT"
/
alter table "EN_DOC_HDR_T" drop column "DTYPE"
/
alter table "EN_ACTN_ITM_T" drop column "DTYPE"
/
alter table "EN_USR_T" drop column "DTYPE"
/
alter table "EN_DOC_TYP_T" drop column "CSTM_ACTN_LIST_ATTRIB_CLS_NM"
/
alter table "EN_DOC_TYP_T" drop column "CSTM_ACTN_EMAIL_ATTRIB_CLS_NM"
/
alter table "EN_DOC_TYP_T" drop column "CSTM_DOC_NTE_ATTRIB_CLS_NM"
/
