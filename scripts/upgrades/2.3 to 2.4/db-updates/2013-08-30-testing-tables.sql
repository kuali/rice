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

CREATE TABLE KRTST_PARENT_GEN_KEY_T
    ( GENERATED_PK_COL NUMBER(8,0) )
/
CREATE TABLE KRTST_PARENT_GEN_KEY_CHILD_T
    ( GENERATED_PK_COL NUMBER(8,0)
    , CHILDS_PK_COL NUMBER(8,0) )
/
CREATE SEQUENCE KRTST_GENERATED_PK_S START WITH 10000
/
CREATE TABLE KRTST_PARENT_OF_UPDATABLE_T
    ( PK_COL NUMBER(8,0),
    UPDATABLE_CHILD_KEY_COL VARCHAR2(10) )
/
CREATE TABLE KRTST_UPDATABLE_CHILD_T
    ( PK_COL VARCHAR2(10), SOME_DATA_COL VARCHAR2(40) )
/
