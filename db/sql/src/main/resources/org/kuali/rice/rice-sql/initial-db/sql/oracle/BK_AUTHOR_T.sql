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

TRUNCATE TABLE BK_AUTHOR_T DROP STORAGE
/
INSERT INTO BK_AUTHOR_T (ACTV_IND,AUTHOR_ID,EMAIL,NM,OBJ_ID,PHONE_NBR,VER_NBR)
  VALUES ('Y',1,'roshan@jimail.com','Roshan Mahanama','a03ad608-84fa-4c89-8410-0a91ed56cb66','123-123-1233',1)
/
INSERT INTO BK_AUTHOR_T (ACTV_IND,AUTHOR_ID,EMAIL,NM,OBJ_ID,PHONE_NBR,VER_NBR)
  VALUES ('Y',2,'jfranklin@jimail.com','James Franklin','a03ad608-84fa-4c89-8410-0a91ed56cb32','999-433-4323',1)
/
