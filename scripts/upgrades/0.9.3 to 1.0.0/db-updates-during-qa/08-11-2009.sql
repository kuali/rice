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

-- This script is part of the effort to clean up the Rice database prior to packaging for public release

-- IMPORTANT: execute the following before running the demo-server-dataset-cleanup.sql

-- KULRICE-3448
update kren_recip_deliv_t set recip_id = 'testuser1' where recip_id = 'TestUser1'
/
update kren_recip_deliv_t set recip_id = 'testuser2' where recip_id = 'TestUser2'
/
update kren_recip_deliv_t set recip_id = 'testuser4' where recip_id = 'TestUser4'
/
update kren_recip_deliv_t set recip_id = 'testuser5' where recip_id = 'TestUser5'
/
update kren_recip_deliv_t set recip_id = 'testuser6' where recip_id = 'TestUser6'
/
update kren_recip_list_t set recip_id = 'testuser1' where recip_id = 'TestUser1'
/
update kren_recip_list_t set recip_id = 'testuser3' where recip_id = 'TestUser3'
/
update kren_rvwer_t set prncpl_id = 'testuser3' where prncpl_id = 'TestUser3'
/
update kren_chnl_subscrp_t set prncpl_id = 'testuser4' where prncpl_id = 'TestUser4'
/

-- KULRICE-3449
insert into KRIM_GRP_MBR_T (GRP_MBR_ID, VER_NBR, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT) VALUES('1207', 1, '6798B3E6C3C49827AE62E5F7A275A1A3', '2000', 'admin', 'P', Null, Null) 
/

