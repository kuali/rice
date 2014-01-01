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

TRUNCATE TABLE KREW_RULE_TMPL_OPTN_T
/
INSERT INTO KREW_RULE_TMPL_OPTN_T (KEY_CD,RULE_TMPL_ID,RULE_TMPL_OPTN_ID,VAL,VER_NBR)
  VALUES ('D','1016','2000','K',1)
/
INSERT INTO KREW_RULE_TMPL_OPTN_T (KEY_CD,RULE_TMPL_ID,RULE_TMPL_OPTN_ID,VAL,VER_NBR)
  VALUES ('A','1016','2001','true',1)
/
INSERT INTO KREW_RULE_TMPL_OPTN_T (KEY_CD,RULE_TMPL_ID,RULE_TMPL_OPTN_ID,VAL,VER_NBR)
  VALUES ('K','1016','2002','true',1)
/
INSERT INTO KREW_RULE_TMPL_OPTN_T (KEY_CD,RULE_TMPL_ID,RULE_TMPL_OPTN_ID,VAL,VER_NBR)
  VALUES ('F','1016','2003','true',1)
/
INSERT INTO KREW_RULE_TMPL_OPTN_T (KEY_CD,RULE_TMPL_ID,RULE_TMPL_OPTN_ID,VAL,VER_NBR)
  VALUES ('C','1016','2004','true',1)
/
INSERT INTO KREW_RULE_TMPL_OPTN_T (KEY_CD,RULE_TMPL_ID,RULE_TMPL_OPTN_ID,VAL,VER_NBR)
  VALUES ('I','1016','2005','some instructions',1)
/
