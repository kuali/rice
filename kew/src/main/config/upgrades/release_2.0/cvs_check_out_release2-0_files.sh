#!/bin/ksh 
#  CHECK_OUT_RELEASE_FILES.SH
# 
#  This script checks out all of the workflow release files from CVS.  
#
#  1)  This step creates the .cvspass file if one doesn't exist.  This file is necessary for logging into CVS.  The CVSROOT
#      environment variable is set with the user, server, and source information. 
#
#  2)  This step creates a redschedule directory in the batch directory if one does not exist.  The workflow release files
#      are checked out of CVS into this redschedule directory.
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
export STARTSTEP=10
export STOPSTEP=20
#add_mail_notification_recipient workflow@indiana.edu

# step to set up CVS root env variable and to create the cvspass file if not already there
function STEP10 {
  set_error_class "008"
  set_error_message "Error in ${SCRIPT_NAME}.  Problem checking into CVS."
  ALWAYS_RETURN=0
  if [[ ! -f ~/.cvspass ]]; then
       do_command "touch ~/.cvspass"
       print_to_log "cvspass file has been created."
  fi
  
#  do_command "export CVSROOT=:pserver:anonymous:guest@wsa123.uits.indiana.edu:/opt/cvs/"
#  print_to_log "CVSROOT has been set - ${CVSROOT}."

}

# step to create redschedule directory and check out workflow release files from CVS into the redschedule directory
function STEP20 {
  set_error_class "008"
  set_error_message "Error in ${SCRIPT_NAME}.  Problem checking out workflow release files into redschedule directory."
  ALWAYS_RETURN=0
  do_command "cd ~/batch"

  if [[ ! -d redschedule ]]; then
       do_command "mkdir redschedule"
       print_to_log "Redschedule directory has been created."
  fi

  print_to_log "Checking out workflow release files."
  do_command "export CVS_RSH=ssh"
  do_command "cvs -d:ext:anonymous@wsa123.uits.indiana.edu:/opt/cvs/ co -d redschedule workflow/conf/release_2.0"
#  do_command "cvs co -d redschedule workflow/conf/release_2.0"

}

############################################################################

   # evaluate our functions in the specified order
   executor

   # call the BRTE function to do final cleanup and exit
   end_run 0
