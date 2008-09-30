-- kr_kim_ent_type_t

INSERT INTO kr_kim_ent_type_t ( ent_typ_cd, ent_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'PERSON', 'Person', '01', SYS_GUID() )
/
INSERT INTO kr_kim_ent_type_t ( ent_typ_cd, ent_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'SYSTEM', 'System', '02', SYS_GUID() )
/
INSERT INTO kr_kim_ent_type_t ( ent_typ_cd, ent_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'VENDOR', 'External Vendor', '03', SYS_GUID() )
/
INSERT INTO kr_kim_ent_type_t ( ent_typ_cd, ent_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'PAYEE', 'Other Payee', '04', SYS_GUID() )
/

-- kr_kim_ent_name_type_t

INSERT INTO kr_kim_ent_name_type_t ( ent_name_typ_cd, ent_name_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'PREFERRED', 'Preferred Name', '01', SYS_GUID() )
/
INSERT INTO kr_kim_ent_name_type_t ( ent_name_typ_cd, ent_name_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'NICKNAME', 'Nickname', '02', SYS_GUID() )
/
INSERT INTO kr_kim_ent_name_type_t ( ent_name_typ_cd, ent_name_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'STREET', 'Street Name', '98', SYS_GUID() )
/
INSERT INTO kr_kim_ent_name_type_t ( ent_name_typ_cd, ent_name_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'AKA', 'Also Known As', '03', SYS_GUID() )
/
INSERT INTO kr_kim_ent_name_type_t ( ent_name_typ_cd, ent_name_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'PEN', 'Pen Name', '99', SYS_GUID() )
/

-- kr_kim_ext_id_type_t

INSERT INTO kr_kim_ext_id_type_t ( ext_id_typ_cd, ext_id_typ_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'LOGON', 'Logon ID', '01', 'N', SYS_GUID() )
/
INSERT INTO kr_kim_ext_id_type_t ( ext_id_typ_cd, ext_id_typ_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'SSN', 'Social Security Number', '02', 'Y', SYS_GUID() )
/
INSERT INTO kr_kim_ext_id_type_t ( ext_id_typ_cd, ext_id_typ_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'TAX', 'Tax ID', '03', 'Y', SYS_GUID() )
/
INSERT INTO kr_kim_ext_id_type_t ( ext_id_typ_cd, ext_id_typ_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'EMPLOYEE', 'Employee ID', '04', 'N', SYS_GUID() )
/
INSERT INTO kr_kim_ext_id_type_t ( ext_id_typ_cd, ext_id_typ_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'HR', 'Human Resources ID', '05', 'N', SYS_GUID() )
/
INSERT INTO kr_kim_ext_id_type_t ( ext_id_typ_cd, ext_id_typ_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'LICENSE', 'Driver''s License', '06', 'N', SYS_GUID() )
/
INSERT INTO kr_kim_ext_id_type_t ( ext_id_typ_cd, ext_id_typ_nm, display_sort_cd, encr_req_ind, obj_id ) 
    VALUES ( 'RFID', 'RFID Implant', '07', 'N', SYS_GUID() )
/

-- kr_kim_email_type_t
INSERT INTO kr_kim_email_type_t ( email_typ_cd, email_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'CAMPUS', 'Campus', '01', SYS_GUID() )
/
INSERT INTO kr_kim_email_type_t ( email_typ_cd, email_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'PERSONAL', 'Personal/Home', '03', SYS_GUID() )
/
INSERT INTO kr_kim_email_type_t ( email_typ_cd, email_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'DEPARTMENT', 'Departmental', '02', SYS_GUID() )
/
INSERT INTO kr_kim_email_type_t ( email_typ_cd, email_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'OTHER', 'Other', '99', SYS_GUID() )
/

-- kr_kim_phone_type_t

INSERT INTO kr_kim_phone_type_t ( phone_typ_cd, phone_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'WORK', 'Work/Office', '01', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_typ_cd, phone_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'HOME', 'Home/Personal', '02', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_typ_cd, phone_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'MOBILE', 'Mobile/Cellular', '03', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_typ_cd, phone_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'FAX', 'Fax', '04', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_typ_cd, phone_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'BUSINESS', 'Business', '05', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_typ_cd, phone_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'PIZZA', 'Favorite Pizza Place''s Number', '06', SYS_GUID() )
/
INSERT INTO kr_kim_phone_type_t ( phone_typ_cd, phone_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'OTHER', 'Other', '99', SYS_GUID() )
/

-- kr_kim_addr_type_t

INSERT INTO kr_kim_addr_type_t ( addr_typ_cd, addr_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'WORK', 'Work', '01', SYS_GUID() )
/
INSERT INTO kr_kim_addr_type_t ( addr_typ_cd, addr_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'HOME', 'Home', '02', SYS_GUID() )
/
INSERT INTO kr_kim_addr_type_t ( addr_typ_cd, addr_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'SUMMER', 'Summer Home', '03', SYS_GUID() )
/
INSERT INTO kr_kim_addr_type_t ( addr_typ_cd, addr_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'YACHT', 'Yacht Harbor Address', '04', SYS_GUID() )
/
INSERT INTO kr_kim_addr_type_t ( addr_typ_cd, addr_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'FAKE', 'Fictional', '05', SYS_GUID() )
/
INSERT INTO kr_kim_addr_type_t ( addr_typ_cd, addr_typ_nm, display_sort_cd, obj_id )
    VALUES ( 'OTHER', 'Other', '99', SYS_GUID() )
/

-- kr_kim_afltn_type_t

INSERT INTO kr_kim_afltn_type_t ( afltn_typ_cd, afltn_typ_nm, emp_afltn_typ_ind, display_sort_cd, obj_id )
    VALUES ( 'STAFF', 'Staff', 'Y', '01', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_typ_cd, afltn_typ_nm, emp_afltn_typ_ind, display_sort_cd, obj_id )
    VALUES ( 'FACULTY', 'Faculty', 'Y', '03', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_typ_cd, afltn_typ_nm, emp_afltn_typ_ind, display_sort_cd, obj_id )
    VALUES ( 'STUDENT', 'Student', 'N', '02', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_typ_cd, afltn_typ_nm, emp_afltn_typ_ind, display_sort_cd, obj_id )
    VALUES ( 'AFFILIATE', 'Affiliate', 'N', '04', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_typ_cd, afltn_typ_nm, emp_afltn_typ_ind, display_sort_cd, obj_id )
    VALUES ( 'ALUMNI', 'Alumni', 'N', '05', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_typ_cd, afltn_typ_nm, emp_afltn_typ_ind, display_sort_cd, obj_id )
    VALUES ( 'BOOK', 'Book Club', 'N', '09', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_typ_cd, afltn_typ_nm, emp_afltn_typ_ind, display_sort_cd, obj_id )
    VALUES ( 'LUG', 'Linux User''s Group', 'N', '08', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_typ_cd, afltn_typ_nm, emp_afltn_typ_ind, display_sort_cd, obj_id )
    VALUES ( 'NACUBO', 'NACUBO', 'N', '06', SYS_GUID() )
/
INSERT INTO kr_kim_afltn_type_t ( afltn_typ_cd, afltn_typ_nm, emp_afltn_typ_ind, display_sort_cd, obj_id )
    VALUES ( 'KUALI', 'Kuali Foundation', 'N', '07', SYS_GUID() )
/

-- kr_kim_emp_stat_t

INSERT INTO kr_kim_emp_stat_t(EMP_STAT_CD, OBJ_ID, EMP_STAT_NM, DISPLAY_SORT_CD)
  VALUES('A', '299A618CB6846130E043814FD8816130', 'Active', '01')
/
INSERT INTO kr_kim_emp_stat_t(EMP_STAT_CD, OBJ_ID, EMP_STAT_NM, DISPLAY_SORT_CD)
  VALUES('D', '299A618CB6856130E043814FD8816130', 'Deceased', '99')
/
INSERT INTO kr_kim_emp_stat_t(EMP_STAT_CD, OBJ_ID, EMP_STAT_NM, DISPLAY_SORT_CD)
  VALUES('L', '299A618CB6866130E043814FD8816130', 'On Non-Pay Leave', '04')
/
INSERT INTO kr_kim_emp_stat_t(EMP_STAT_CD, OBJ_ID, EMP_STAT_NM, DISPLAY_SORT_CD)
  VALUES('N', '299A618CB6876130E043814FD8816130', 'Status Not Yet Processed', '03')
/
INSERT INTO kr_kim_emp_stat_t(EMP_STAT_CD, OBJ_ID, EMP_STAT_NM, DISPLAY_SORT_CD)
  VALUES('P', '299A618CB6886130E043814FD8816130', 'Processing', '02')
/
INSERT INTO kr_kim_emp_stat_t(EMP_STAT_CD, OBJ_ID, EMP_STAT_NM, DISPLAY_SORT_CD)
  VALUES('R', '299A618CB6896130E043814FD8816130', 'Retired', '10')
/
INSERT INTO kr_kim_emp_stat_t(EMP_STAT_CD, OBJ_ID, EMP_STAT_NM, DISPLAY_SORT_CD)
  VALUES('T', '299A618CB68A6130E043814FD8816130', 'Terminated', '97')
/
INSERT INTO kr_kim_emp_stat_t(EMP_STAT_CD, OBJ_ID, EMP_STAT_NM, DISPLAY_SORT_CD)
  VALUES('X', '299A618CB68B6130E043814FD8816130', 'Retired 2', '11')
/

-- kr_kim_emp_type_t

INSERT INTO kr_kim_emp_type_t(EMP_TYP_CD, OBJ_ID, EMP_TYP_NM, DISPLAY_SORT_CD)
  VALUES('N', '299A618CB68D6130E043814FD8816130', 'Non-Professional', '02')
/
INSERT INTO kr_kim_emp_type_t(EMP_TYP_CD, OBJ_ID, EMP_TYP_NM, DISPLAY_SORT_CD)
  VALUES('O', '299A618CB68E6130E043814FD8816130', 'Other', '99')
/
INSERT INTO kr_kim_emp_type_t(EMP_TYP_CD, OBJ_ID, EMP_TYP_NM, DISPLAY_SORT_CD)
  VALUES('P', '299A618CB68F6130E043814FD8816130', 'Professional', '01')
/
