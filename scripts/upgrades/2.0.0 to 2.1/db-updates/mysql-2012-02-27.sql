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


--
-- KULRICE-6842: Don't allow requests for null principals or null groups or null principal types
-- or null roles.
--

ALTER TABLE `KRIM_GRP_MBR_T`
MODIFY COLUMN `GRP_ID` VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
MODIFY COLUMN `MBR_ID` VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
MODIFY COLUMN `MBR_TYP_CD` CHAR(1) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;

ALTER TABLE `KRIM_ROLE_MBR_T`
MODIFY COLUMN `ROLE_ID` VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
MODIFY COLUMN `MBR_ID` VARCHAR(40) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
MODIFY COLUMN `MBR_TYP_CD` CHAR(1) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
