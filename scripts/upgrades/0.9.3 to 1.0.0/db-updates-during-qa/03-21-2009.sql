--
-- Copyright 2005-2011 The Kuali Foundation
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

DECLARE
CURSOR cursor1 IS
	SELECT RULE_ID FROM KREW_RULE_T WHERE NM IS NULL AND CUR_IND=1;
BEGIN
	FOR r IN cursor1 LOOP
        execute immediate 'UPDATE KREW_RULE_T SET NM=SYS_GUID() WHERE RULE_ID='||r.RULE_ID;
    END LOOP;
END;
/

DECLARE
CURSOR cursor1 IS
	SELECT PREV.RULE_ID, RULE.NM FROM KREW_RULE_T PREV, KREW_RULE_T RULE
    WHERE PREV.RULE_ID=RULE.PREV_RULE_VER_NBR AND RULE.NM IS NOT NULL;
cnt NUMBER := 0;
BEGIN
    LOOP
        FOR r IN cursor1 LOOP
            UPDATE KREW_RULE_T SET NM=r.NM WHERE RULE_ID=r.RULE_ID;
        END LOOP;
        cnt := cnt + 1;
        IF cnt > 1000 THEN EXIT; END IF;
    END LOOP;
END;
/

UPDATE KREW_RULE_T RULE
SET (NM) =
(
  SELECT PREV.NM
  FROM KREW_RULE_T PREV
  WHERE PREV.RULE_ID=RULE.PREV_RULE_VER_NBR
  AND PREV.NM IS NOT NULL
)
WHERE RULE.NM IS NULL

ALTER TABLE KREW_RULE_T MODIFY NM NOT NULL
/
