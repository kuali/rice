-- KULRICE-4517 - Needed for effective dating. This is a travel app table
CREATE TABLE TRV_ACCT_USE_RT_T ( 
    ID VARCHAR(40) NOT NULL, 
    ACCT_NUM VARCHAR(10) NOT NULL, 
    RATE DECIMAL(8,2) NOT NULL, 
    ACTV_FRM_DT DATETIME NULL, 
    ACTV_TO_DT DATETIME NULL, 
    PRIMARY KEY(ID) 
)
/
