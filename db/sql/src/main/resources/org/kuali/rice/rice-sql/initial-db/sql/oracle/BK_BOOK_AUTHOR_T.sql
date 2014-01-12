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

TRUNCATE TABLE BK_BOOK_AUTHOR_T DROP STORAGE
/
INSERT INTO BK_BOOK_AUTHOR_T (AUTHOR_ID,BOOK_ID)
  VALUES (1,1)
/
INSERT INTO BK_BOOK_AUTHOR_T (AUTHOR_ID,BOOK_ID)
  VALUES (1,2)
/
INSERT INTO BK_BOOK_AUTHOR_T (AUTHOR_ID,BOOK_ID)
  VALUES (2,1)
/
