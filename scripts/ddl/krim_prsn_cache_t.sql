CREATE TABLE krim_prsn_cache_t (
	prncpl_id		VARCHAR(40),
	prncpl_nm		VARCHAR(40),
	entity_id		VARCHAR(40),
	entity_typ_cd		VARCHAR(40),
	first_nm		VARCHAR(40),
	middle_nm		VARCHAR(40),
	last_nm		VARCHAR(40),
	prsn_nm		VARCHAR(40),
	campus_cd		VARCHAR(40),
	prmry_dept_cd		VARCHAR(40),
	emp_id		VARCHAR(40),
	last_updt_ts		TIMESTAMP,
	obj_id		VARCHAR2(36) NOT NULL,
	CONSTRAINT krim_prsn_cache_tp1 PRIMARY KEY ( prncpl_id )
)
/
