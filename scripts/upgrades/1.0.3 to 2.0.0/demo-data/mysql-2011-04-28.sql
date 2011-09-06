--
-- KULRICE-4794
-- The following statements will change the DOC_HDR_ID from a decimal to a VARCHAR(40) on TRV_DOC_ACCT. 
--

ALTER TABLE TRV_DOC_ACCT CHANGE DOC_HDR_ID DOC_HDR_ID VARCHAR(40) NOT NULL
