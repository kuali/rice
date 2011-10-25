update KRIM_ROLE_T set ROLE_NM=CONCAT(ROLE_NM, CONCAT(' ', ROLE_ID)) where (NMSPC_CD, ROLE_NM) in (
  select NMSPC_CD, ROLE_NM
  from KRIM_ROLE_T
  group by NMSPC_CD, ROLE_NM
  having count(*) > 1
)