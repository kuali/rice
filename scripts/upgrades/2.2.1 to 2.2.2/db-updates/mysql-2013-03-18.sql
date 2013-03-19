
--
-- KULRICE-9152: PK for krad_msg_t is too long for MySQL 5.1
--

ALTER TABLE krad_msg_t MODIFY loc varchar(80) not null
;