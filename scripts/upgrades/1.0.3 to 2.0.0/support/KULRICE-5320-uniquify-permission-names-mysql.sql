update KRIM_PERM_T as TGT set NM=CONCAT(TGT.NM, ' ', TGT.PERM_ID) where (TGT.NMSPC_CD, TGT.NM) in (
  select NMSPC_CD, NM from (
    select SRC.NMSPC_CD, SRC.NM
    from KRIM_PERM_T as SRC
    group by SRC.NMSPC_CD, SRC.NM
    having count(*) > 1
  ) as tmptable
)