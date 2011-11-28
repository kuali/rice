update krim_perm_t
   set nmspc_cd = 'KRMS_TEST'
 where nm = 'Maintain KRMS Agenda'
   and nmspc_cd = 'KR-RULE'
/

delete from krim_perm_attr_data_t
 where perm_id = (select perm_id from krim_perm_t where nm = 'Maintain KRMS Agenda' and nmspc_cd = 'KRMS_TEST')
/