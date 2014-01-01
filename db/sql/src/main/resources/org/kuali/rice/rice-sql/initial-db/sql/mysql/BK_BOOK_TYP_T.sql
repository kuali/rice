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

TRUNCATE TABLE BK_BOOK_TYP_T
/
INSERT INTO BK_BOOK_TYP_T (ACTV_IND,DESC_TXT,NM,OBJ_ID,TYP_CD,VER_NBR)
  VALUES ('Y','Romantic Books','Romantic','6bbbdb82-d614-49c2-8716-4234e72f9f5e','ROM',1)
/
INSERT INTO BK_BOOK_TYP_T (ACTV_IND,DESC_TXT,NM,OBJ_ID,TYP_CD,VER_NBR)
  VALUES ('Y','Science Fiction Story','Science Fiction','482b3394-0327-4e93-bd80-c5dc3b2a9e1f','SCI-FI',1)
/
