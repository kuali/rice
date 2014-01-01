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

TRUNCATE TABLE BK_BOOK_T DROP STORAGE
/
INSERT INTO BK_BOOK_T (BOOK_ID,ISBN,OBJ_ID,PRICE,PUBLISHER,PUB_DATE,RATING,TITLE,TYP_CD,VER_NBR)
  VALUES (1,'9781402894626','482b3394-0327-4e93-bd80-c5dc3b2a9e34',34.43,'Rupa Publishers Ltd.',TO_DATE( '20020901000000', 'YYYYMMDDHH24MISS' ),87,'i See','ROM',1)
/
INSERT INTO BK_BOOK_T (BOOK_ID,ISBN,OBJ_ID,PRICE,PUBLISHER,PUB_DATE,RATING,TITLE,TYP_CD,VER_NBR)
  VALUES (2,'9781402894634','482b3394-0327-4ee5-bd80-c5dc3b2a9e34',12.43,'Rupa Publishers Ltd.',TO_DATE( '20020901000000', 'YYYYMMDDHH24MISS' ),90,'Galactico','SCI-FI',1)
/
