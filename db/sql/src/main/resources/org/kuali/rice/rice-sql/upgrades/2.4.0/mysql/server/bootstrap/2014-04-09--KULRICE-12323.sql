-- This alters the size of the address fields on the address and person maintenance document tables so they are a bit longer and match
ALTER TABLE krim_entity_addr_t CHANGE COLUMN ADDR_LINE_1 ADDR_LINE_1 VARCHAR(128)
/
ALTER TABLE krim_entity_addr_t CHANGE COLUMN ADDR_LINE_2 ADDR_LINE_2 VARCHAR(128)
/
ALTER TABLE krim_entity_addr_t CHANGE COLUMN ADDR_LINE_3 ADDR_LINE_3 VARCHAR(128)
/

ALTER TABLE krim_pnd_addr_mt CHANGE COLUMN ADDR_LINE_1 ADDR_LINE_1 VARCHAR(128)
/
ALTER TABLE krim_pnd_addr_mt CHANGE COLUMN ADDR_LINE_2 ADDR_LINE_2 VARCHAR(128)
/
ALTER TABLE krim_pnd_addr_mt CHANGE COLUMN ADDR_LINE_3 ADDR_LINE_3 VARCHAR(128)
/
