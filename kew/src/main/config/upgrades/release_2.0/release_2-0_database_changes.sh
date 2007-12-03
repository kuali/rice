#!/bin/ksh 
#  RELEASE_2-0_DATABASE_CHANGES.SH
# 
#  This script performs the database changes for workflow release 2.0. 
#
#  Run script with an environment parameter of dev, cnv, unt, reg, stg, trn, or prd. 
#
#  set ORA_SERVERS, DBUSER, DATASERVER, STAGINGDIR in Brte profile  
#
#
#  The basic functions performed by this script are:
#  1)  removes new release 2.0 workflow database objects. This is used for restore capabilities
#          if the script fails when creating these objects.
#
#  2)  creates new release 2.0 workflow database objects - tables, indexes, sequences.
#
#  3)  runs alter table statements against existing workflow tables.  Alter statements to reverse these changes are commented out
#          at the top of the file executed in this step and can be uncommented to reverse any of the changes.
#
#  4)  updates table rows and inserts new bootstrap data.  This includes inserting new Application Constants.
#
#  5)  deletes pre-release1.6 tables and tables migrated during the 1.6 release but are no longer used.  It also deletes sequences
#          not used anymore.
#
#
#  ERROR CHECKING
#  ______________
#
#  This script checks the return status of every command or script
#  that is executed.  If any return status is greater than zero,
#  this script will terminate and issue an error message
#  to /dev/console.
#
#
#  LOGGING
#  ______________
#
#  This script checks the return status of every command or script
#  that is executed.
#
#********************************************************
# Execute the script
#********************************************************

. /opt/brte/batch_modules

# Set up variables for logging and error handling
#
set_error_class "008"
set_error_message "Error in ${SCRIPT_NAME}.  See DS.CALL for instructions."
export ENVIRON=$1
export STARTSTEP=20
export STOPSTEP=50
#add_mail_notification_recipient workflow@indiana.edu

# step to remove new release 2.0 workflow database objects
function STEP10 {
  set_error_class "008"
  set_error_message "Error in ${SCRIPT_NAME}.  Problem dropping new workflow database objects."
  ALWAYS_RETURN=0
  do_sql_file -u ${DBUSER} ${HOME}/batch/redschedule/drop_database_objects_release2-0.sql ${DATASERVER}

}

# step to create new release 2.0 workflow database objects
function STEP20 {
  set_error_class "008"
  set_error_message "Error in ${SCRIPT_NAME}.  Problem creating new workflow database objects."
  ALWAYS_RETURN=0
  do_sql_file -u ${DBUSER} ${HOME}/batch/redschedule/create_database_objects_release2-0.sql ${DATASERVER}

}

# step to run alter table statements against existing workflow tables
function STEP30 {
  set_error_class "008"
  set_error_message "Error in ${SCRIPT_NAME}.  Problem altering existing workflow tables."
  ALWAYS_RETURN=0
  do_sql_file -u ${DBUSER} ${HOME}/batch/redschedule/alter_workflow_tables_release2-0.sql ${DATASERVER}
  
}

# step to update table rows and insert new bootstrap data into workflow tables
function STEP40 {
  set_error_class "008"
  set_error_message "Error in ${SCRIPT_NAME}.  Problem with updating and inserting workflow table data."
  ALWAYS_RETURN=0
  case ${ENVIRON} in
            dev) export CONTEXT="http://localhost:8080/en-dev/";;
            cnv) export CONTEXT="https://es-nd.ucs.indiana.edu:9000/en-cnv/";;
            unt) export CONTEXT="https://es-nd.ucs.indiana.edu:9000/en-unt/";;
            reg) export CONTEXT="https://es-nd.ucs.indiana.edu:9000/en-reg/";;
            stg) export CONTEXT="https://es-nd.ucs.indiana.edu:9000/en-stg/";;
            trn) export CONTEXT="https://es-nd.ucs.indiana.edu:9000/en-trn/";;
            snd) export CONTEXT="https://es-nd.ucs.indiana.edu:9000/en-snd/";;
            prd) export CONTEXT="https://uisapp2.iu.edu/en-prd/";; 
            *)  print_to_log "Invalid environment value"; return 8;;
  esac

  do_sql_command -u ${DBUSER} "@${HOME}/batch/redschedule/change_workflow_data_release2-0.sql ${CONTEXT}" ${DATASERVER}

}

# step to drop the pre-release1.6 tables and unused sequences and some migrated 1.6 tables
function STEP50 {
  set_error_class "008"
  set_error_message "Error in ${SCRIPT_NAME}.  Problem dropping the old workflow database objects."
  ALWAYS_RETURN=0
  do_sql_file -u ${DBUSER} ${HOME}/batch/redschedule/drop_old_workflow_tables.sql ${DATASERVER}

}

############################################################################

   # evaluate our functions in the specified order
   executor

   # call the BRTE function to do final cleanup and exit
   end_run 0
