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

CREATE TABLE KRTST_TYP_PARENT_T (
  CHAR_PROP CHAR(1),
  BOOL_PROP VARCHAR(1),
  SHORT_PROP DECIMAL(5,0),
  INT_PROP DECIMAL(10,0),
  LONG_PROP DECIMAL(20,0),
  FLOAT_PROP DECIMAL(10,2),
  DOUBLE_PROP DECIMAL(20,2)
)
/

CREATE TABLE KRTST_TYP_CHILD_T (
  CHAR_PROP CHAR(1),
  BOOL_PROP VARCHAR(1),
  SHORT_PROP DECIMAL(5,0),
  INT_PROP DECIMAL(10,0),
  LONG_PROP DECIMAL(20,0),
  FLOAT_PROP DECIMAL(10,2),
  DOUBLE_PROP DECIMAL(20,2)
)
/
