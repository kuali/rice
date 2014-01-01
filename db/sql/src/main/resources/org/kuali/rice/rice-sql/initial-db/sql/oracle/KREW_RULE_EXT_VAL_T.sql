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

TRUNCATE TABLE KREW_RULE_EXT_VAL_T DROP STORAGE
/
INSERT INTO KREW_RULE_EXT_VAL_T (KEY_CD,RULE_EXT_ID,RULE_EXT_VAL_ID,VAL,VER_NBR)
  VALUES ('destination','1047','1048','las vegas',1)
/
INSERT INTO KREW_RULE_EXT_VAL_T (KEY_CD,RULE_EXT_ID,RULE_EXT_VAL_ID,VAL,VER_NBR)
  VALUES ('campus','1104','1105','IUB',1)
/
INSERT INTO KREW_RULE_EXT_VAL_T (KEY_CD,RULE_EXT_ID,RULE_EXT_VAL_ID,VAL,VER_NBR)
  VALUES ('campus','1107','1108','IUPUI',1)
/
