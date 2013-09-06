
--
-- KULRICE-9998: Country Name is defined as 40 characters in the database, but 50 characters in the data dictionary
--

ALTER TABLE krlc_cntry_t MODIFY ( postal_cntry_nm VARCHAR2(255) )
/
