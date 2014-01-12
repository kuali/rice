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


--
-- KULRICE-11415  KRMS Rules - cannot save more than 2 rules
-- KULRICE-11409  KRMS - Cannot delete or update additional False rule from rule editor
--

--
-- These constraints only cause issues in oracle so there are no corresponding updates for mysql
--

ALTER TABLE krms_agenda_itm_t DROP CONSTRAINT krms_agenda_itm_fk4
/

ALTER TABLE krms_agenda_itm_t ADD CONSTRAINT krms_agenda_itm_fk4
    FOREIGN KEY (when_true )
    REFERENCES krms_agenda_itm_t (agenda_itm_id )
    ON DELETE SET NULL
/

ALTER TABLE krms_agenda_itm_t DROP CONSTRAINT krms_agenda_itm_fk5
/

ALTER TABLE krms_agenda_itm_t ADD CONSTRAINT krms_agenda_itm_fk5
    FOREIGN KEY (when_false )
    REFERENCES krms_agenda_itm_t (agenda_itm_id )
    ON DELETE SET NULL
/

ALTER TABLE krms_agenda_itm_t DROP CONSTRAINT krms_agenda_itm_fk6
/

ALTER TABLE krms_agenda_itm_t ADD CONSTRAINT krms_agenda_itm_fk6
    FOREIGN KEY (always )
    REFERENCES krms_agenda_itm_t (agenda_itm_id )
    ON DELETE SET NULL
/
