update KRIM_ROLE_T as TGT set ROLE_NM=CONCAT(TGT.ROLE_NM, ' ', TGT.ROLE_ID) where (TGT.NMSPC_CD, TGT.ROLE_NM) in (
  select NMSPC_CD, ROLE_NM from (
    select SRC.NMSPC_CD, SRC.ROLE_NM
    from KRIM_ROLE_T as SRC
    group by SRC.NMSPC_CD, SRC.ROLE_NM
    having count(*) > 1
  ) as tmptable
)