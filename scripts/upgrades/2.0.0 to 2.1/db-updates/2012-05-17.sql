
-- KULRICE-7237: KRNS_NTE_T is selected by a field with no indexes - full table scan every time
create index KRNS_NTE_TI1 on KRNS_NTE_T (RMT_OBJ_ID);