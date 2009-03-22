-- KULRICE-2870 - generate names for all existing rules that don't have one
-- if I was better at PL/SQL i could probably figure out a better way to do
-- this, but it will basically set up and generate rule names for rules with
-- up to 1000 different versions, if you have rules with more versions than
-- that, you can update the loop condition

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

ALTER TABLE KREW_RULE_T MODIFY NM NOT NULL
/