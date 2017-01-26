--
-- Copyright 2005-2017 The Kuali Foundation
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

-- KULRICE-7128 wire kim type attribute 'qualifierResolverProvidedIdentifier' to Responsibility type
Insert into krim_typ_attr_t
(KIM_TYP_ATTR_ID,
OBJ_ID,
VER_NBR,
SORT_CD,
KIM_TYP_ID,
KIM_ATTR_DEFN_ID,
ACTV_IND)
VALUES
  ((select (max(to_number(KIM_TYP_ATTR_ID)) + 1) from  krim_typ_attr_t where KIM_TYP_ATTR_ID is not NULL and REGEXP_LIKE(KIM_TYP_ATTR_ID, '^[1-9][0-9]*$') and to_number(KIM_TYP_ATTR_ID) < 10000),
  '69FA55ACC2EE2598E0404F8189D86880',
  1,
  'e',
  7,
  46,
  'Y')
/
