ALTER TABLE TRV_ACCT ADD (SUB_ACCT VARCHAR2(10))
/
ALTER TABLE TRV_ACCT ADD (SUB_ACCT_NAME VARCHAR2(50))
/
ALTER TABLE TRV_ACCT ADD (CREATE_DT DATE)
/
ALTER TABLE TRV_ACCT ADD (SUBSIDIZED_PCT FLOAT)
/
update trv_acct set sub_acct = concat(acct_num, '-sub'),
    sub_acct_name = concat('Sub Account for ', acct_name),
    create_dt=SYSTIMESTAMP,
    subsidized_pct = dbms_random.value(1,100) 
/