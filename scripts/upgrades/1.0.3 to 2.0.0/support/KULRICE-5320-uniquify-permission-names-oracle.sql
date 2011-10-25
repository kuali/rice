update KRIM_PERM_T set NM=CONCAT(NM, CONCAT(' ', PERM_ID)) where (NMSPC_CD, NM) in (
  select NMSPC_CD, NM
  from KRIM_PERM_T
  group by NMSPC_CD, NM
  having count(*) > 1
)