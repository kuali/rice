
-- -----------------------------------------------------
-- Table `krms_cntxt_vld_agenda_t`
-- -----------------------------------------------------


CREATE  TABLE IF NOT EXISTS `krms_cntxt_vld_agenda_t` (
  `cntxt_vld_agenda_id` VARCHAR(40) NOT NULL ,
  `cntxt_id` VARCHAR(40) NOT NULL ,
  `agenda_typ_id` VARCHAR(40) NOT NULL ,
  `ver_nbr` DECIMAL NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`cntxt_vld_agenda_id`) ,
  INDEX `krms_cntxt_vld_agenda_ti1` (`cntxt_id` ASC) ,
  CONSTRAINT `krms_cntxt_vld_agenda_fk1`
    FOREIGN KEY (`cntxt_id` )
    REFERENCES `krms_cntxt_t` (`cntxt_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

create table krms_cntxt_vld_agenda_s ( 
  id bigint(19) not null auto_increment, 
  primary key (id) 
) ENGINE MyISAM; 
alter table krms_cntxt_vld_agenda_s auto_increment = 1000;