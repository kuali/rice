--
-- Copyright 2005-2013 The Kuali Foundation
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

-- Traveler Detail Maintenance Document Type 
-- KULRICE-11133 Samples for M44
INSERT INTO KREW_DOC_TYP_T(DOC_TYP_ID, PARNT_ID, DOC_TYP_NM, DOC_TYP_VER_NBR, ACTV_IND, CUR_IND, LBL, DOC_TYP_DESC, DOC_HDLR_URL, POST_PRCSR, RTE_VER_NBR, VER_NBR, OBJ_ID) VALUES (
'KRSAP1001', 'KRSAP1000', 'TravelerDetailMaintenanceDocument', 0, 1, 1, 'Travel Traveler Detail Maintenance Document', 'Create a Traveler Detail Maintenance Document', '${application.url}/kr-krad/maintenance?methodToCall=docHandler&dataObjectClassName=edu.sampleu.travel.dataobject.TravelerDetail', 'org.kuali.rice.krad.workflow.postprocessor.KualiPostProcessor', 2, 1, uuid() )
/

