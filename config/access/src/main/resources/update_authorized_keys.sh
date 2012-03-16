#!/bin/bash -xe
# Update authorized keys from SVN for servers
# Script must be called from Jenkins as it sets $WORKSPACE

KEYFILE="$WORKSPACE/src/main/resources/authorized_keys"
PRIVATEKEY=/var/lib/jenkins/.ssh/kr-key.pem

# check return code and exit if not zero
check_ret_code() {
  ret_code=$?
  if [ $ret_code -ne 0 ]; then
    printf "\n\nReturn code is $ret_code.  Exiting.....\n\n";  
    exit $ret_code
  fi
}

hosts=( 
ubuntu@ci.rice.kuali.org
ec2-user@deploy1.rice.kuali.org
ec2-user@deploy2.rice.kuali.org
ec2-user@deploy3.rice.kuali.org
)

# Make sure key file exists and it is not zero byte
if [[ ! -s $KEYFILE ]]; then
   echo "Unable to checkout $KEYFILE or it is zero byte"
   exit 1
fi

# Update servers with the key
for host in ${hosts[@]}; do
   echo Updating $host
   ssh -i $PRIVATEKEY $host "chmod 600 ~/.ssh/authorized_keys"
   check_ret_code
   scp -i $PRIVATEKEY $KEYFILE $host:.ssh/.
   check_ret_code
done

echo Updated authorized_keys successfully.
