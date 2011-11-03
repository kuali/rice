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

UPDATE krew_rule_t SET RULE_TMPL_ID='1030' WHERE `RULE_ID`='1049'
/
UPDATE krew_rule_t SET RULE_TMPL_ID='1032' WHERE `RULE_ID`='1051'
/
DELETE FROM krew_rule_t WHERE RULE_ID='1052'
/
UPDATE krew_rule_rsp_t SET RULE_ID='1050' WHERE `RULE_RSP_ID`='2029'
/
UPDATE krew_rule_rsp_t SET RULE_ID='1051' WHERE `RULE_RSP_ID`='2031'
/