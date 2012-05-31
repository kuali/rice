-- 
-- Copyright 2009 The Kuali Foundation
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
-- change namespace to KUALI on all groups with namespace of KFS from original EN_WRKGRP_T to KRIM_GRP_T conversion

UPDATE KRIM_GRP_T set NMSPC_CD='KUALI' where NMSPC_CD='KFS'
/

alter table KRNS_ATT_T modify MIME_TYP VARCHAR2(150)
/

DROP TABLE KRNS_DOC_TYP_ATTR_T
/
