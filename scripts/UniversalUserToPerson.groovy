import java.util.regex.Matcher
import java.util.regex.Pattern

//def baseDir = '/kuali/projects/rice'
def baseDir = '/java/projects/rice-release-0-9-4-kfs-080813-br'

def sourceDirectories = [
    '/impl/src/main/', 
    '/api/src/main/',
    '/web/src/main/',
    '/kns/src/test/',
    '/kcb/src/test/',
    '/ken/src/test/',
    '/ksb/src/test/',
    '/kew/src/test/',
    '/web/src/test/'
]

def filesToDelete = [
    '/impl/src/main/java/org/kuali/rice/kns/bo/user/AuthenticationUserId.java',
    '/impl/src/main/java/org/kuali/rice/kns/bo/user/PersonPayrollId.java',
    '/impl/src/main/java/org/kuali/rice/kns/bo/user/PersonTaxId.java',
    '/impl/src/main/java/org/kuali/rice/kns/bo/user/UniversalUser.java',
    '/impl/src/main/java/org/kuali/rice/kns/bo/user/UserId.java',
    '/impl/src/main/java/org/kuali/rice/kns/bo/user/Uuid.java',
    '/impl/src/main/java/org/kuali/rice/kns/bo/user/KualiGroup.java',
    '/impl/src/main/java/org/kuali/rice/kns/service/KualiGroupService.java',
    '/impl/src/main/java/org/kuali/rice/kns/service/impl/KualiGroupServiceImpl.java',
    '/kns/src/test/java/org/kuali/rice/kns/service/KualiGroupServiceTest.java',
    '/impl/src/main/java/org/kuali/rice/kns/bo/user/KualiUniversalGroup.java',
    '/impl/src/main/java/org/kuali/rice/kns/dao/impl/UniversalUserDaoJpa.java',
    '/impl/src/main/java/org/kuali/rice/kns/dao/impl/UniversalUserDaoOjb.java',
    '/impl/src/main/java/org/kuali/rice/kns/dao/proxy/UniversalUserDaoProxy.java',
    '/impl/src/main/java/org/kuali/rice/kns/dao/RiceKNSDefaultUserDAOImpl.java',
    '/impl/src/main/java/org/kuali/rice/kns/dao/UniversalUserDao.java',
    '/impl/src/main/java/org/kuali/rice/kns/document/authorization/UniversalUserDocumentAuthorizer.java',
    '/impl/src/main/java/org/kuali/rice/kns/lookup/UniversalUserLookupableHelperServiceImpl.java',
    '/impl/src/main/java/org/kuali/rice/kns/lookup/UniversalUserLookupableImpl.java',
    '/impl/src/main/java/org/kuali/rice/kns/maintenance/UniversalUserMaintainable.java',
    '/impl/src/main/java/org/kuali/rice/kns/service/impl/UniversalUserServiceImpl.java',
    '/impl/src/main/java/org/kuali/rice/kns/service/UniversalUserService.java',
    '/impl/src/main/java/org/kuali/rice/kns/rules/UniversalUserRule.java',
    '/impl/src/main/java/org/kuali/rice/kns/rules/UniversalUserPreRules.java',
    '/impl/src/main/java/org/kuali/rice/kns/authorization/UniversalUserAuthorizationConstants.java'
]

replacements = [
    // import replacements - these need to be before the rest of the replacements
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.AuthenticationUserId;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.PersonPayrollId;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.PersonTaxId;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.UniversalUser;', 'import org\\.kuali\\.rice\\.kim\\.bo\\.Person;'],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.UserId;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.Uuid;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.KualiGroup;', 'import org\\.kuali\\.rice\\.kim\\.bo\\.group\\.KimGroup;'],
    ['import org\\.kuali\\.rice\\.kns\\.service\\.impl\\.KualiGroupServiceImpl;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.service\\.KualiGroupService;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.bo\\.user\\.KualiUniversalGroup;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.dao\\.impl\\.UniversalUserDaoJpa;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.dao\\.impl\\.UniversalUserDaoOjb;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.dao\\.proxy\\.UniversalUserDaoProxy;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.dao\\.RiceKNSDefaultUserDAOImpl;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.dao\\.UniversalUserDao;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.document\\.authorization\\.UniversalUserDocumentAuthorizer;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.lookup\\.UniversalUserLookupableHelperServiceImpl;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.lookup\\.UniversalUserLookupableImpl;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.maintenance\\.UniversalUserMaintainable;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.service\\.impl\\.UniversalUserServiceImpl;', 'import org\\.kuali\\.rice\\.kim\\.service\\.impl\\.PersonServiceImpl;'],
    ['import org\\.kuali\\.rice\\.kns\\.service\\.UniversalUserService;', 'import org\\.kuali\\.rice\\.kim\\.service\\.PersonService;'],
    ['import org\\.kuali\\.rice\\.kns\\.rules\\.UniversalUserRule;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.rules\\.UniversalUserPreRules;', ''],
    ['import org\\.kuali\\.rice\\.kns\\.authorization\\.UniversalUserAuthorizationConstants;', ''],    
    // done with imports
    
    [ 'KNSServiceLocator\\.getUniversalUserService\\(\\)\\.getUniversalUser\\(', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.getPerson\\(' ],
    [ 'KNSServiceLocator\\.getUniversalUserService\\(\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)' ],
    [ 'DataDictionary\\.UniversalUser\\.attributes\\.personUserIdentifier', 'DataDictionary\\.PersonImpl\\.attributes\\.principalName' ],
    [ 'DataDictionary\\.UniversalUser\\.attributes', 'DataDictionary\\.PersonImpl\\.attributes' ],
    [ 'universalUser\\.personFirstName', 'person\\.firstName' ],
    [ 'universalUser\\.personMiddleName', 'person\\.middleName' ],
    [ 'universalUser\\.personLastName', 'person\\.lastName' ],
    [ 'universalUserService\\.getUniversalUserByAuthenticationUserId', 'personService\\.getPersonByPrincipalName' ],
    [ 'UserService\\.getUniversalUserByAuthenticationUserId', 'PersonService\\.getPersonByPrincipalName' ],
    [ 'UserService\\.js', 'PersonService\\.js' ],
    [ 'org\\.kuali\\.rice\\.kns\\.bo\\.user\\.UniversalUser', 'org\\.kuali\\.rice\\.kim\\.bo\\.Person' ],
    [ 'org\\.kuali\\.rice\\.kns\\.service\\.UniversalUserService', 'org\\.kuali\\.rice\\.kim\\.service\\.PersonService' ],
    [ '\\.getPersonUniversalIdentifier\\(\\)', '\\.getPrincipalId\\(\\)' ],
    [ '\\.getPersonUserIdentifier\\(\\)', '\\.getPrincipalName\\(\\)' ],
    [ '\\.getPersonName\\(\\)', '\\.getName\\(\\)' ],
    [ 'resolveUserIdentifiersToUniversalIdentifiers', 'resolvePrincipalNamesToPrincipalIds' ],
    [ '\\bUniversalUserService\\b', 'org\\.kuali\\.rice\\.kim\\.service\\.PersonService' ],
    [ 'UniversalUserService', 'PersonService' ],
    [ 'universalUserService', 'personService' ],
    [ 'universalUserService\\.getUniversalUser\\(', 'personService\\.getPerson\\(' ],   
    [ 'new UniversalUser\\(', 'new org\\.kuali\\.rice\\.kim\\.bo\\.impl\\.PersonImpl\\(' ],
    [ 'UniversalUser\\.class\\.isAssignableFrom\\(referenceClass\\) \\|\\| ', '' ],
    [ 'UniversalUser', 'Person' ],
    [ 'universalUser', 'person' ],
    [ 'personUniversalIdentifier', 'principalId' ],
    [ 'personUserIdentifier', 'principalName' ],
    [ 'getPersonPayrollIdentifier\\(\\)', 'getExternalIdentifier\\( KimConstants\\.EMPLOYEE_EXT_ID_TYPE \\)' ],
    [ 'getPersonTaxIdentifier\\(\\)', 'getExternalIdentifier\\( KimConstants\\.TAX_EXT_ID_TYPE \\)' ],
    [ 'getPersonEmailAddress\\(\\)', 'getEmailAddress\\(\\)' ],
    [ 'getPersonFirstName\\(\\)', 'getFirstName\\(\\)' ],
    [ 'getPersonLastName\\(\\)', 'getLastName\\(\\)' ],
    [ 'getPersonCampusAddress\\(\\)', 'getAddress\\.getLine1\\(\\)' ],
    [ 'getPersonLocalPhoneNumber\\(\\)', 'getPhoneNumber\\(\\)' ],
    [ 'getCampusCode\\(\\)', 'getCampusCode\\(\\)' ],
    [ 'getPersonMiddleName\\(\\)', 'getMiddleName\\(\\)' ],
    [ 'isAffiliate\\(\\)', 'hasAffiliationOfType\\( KimConstants\\.AFFILIATE_AFFILIATION_TYPE \\)' ],
    [ 'isFaculty\\(\\)', 'hasAffiliationOfType\\( KimConstants\\.FACULTY_AFFILIATION_TYPE \\)' ],
    [ 'isStaff\\(\\)', 'hasAffiliationOfType\\( KimConstants\\.STAFF_AFFILIATION_TYPE \\)' ],
    [ 'isStudent\\(\\)', 'hasAffiliationOfType\\( KimConstants\\.STUDENT_AFFILIATION_TYPE \\)' ],
    [ 'new KualiGroup\\(', 'new org\\.kuali\\.rice\\.kim\\.bo\\.group\\.impl\\.KimGroupImpl\\(' ],
    [ 'getPersonService\\(\\)\\.getPersonByAuthenticationUserId\\(', 'getPersonService\\(\\)\\.getPersonByPrincipalName\\(' ],    
    [ 'KUALI_GROUP_SERVICE', 'KIM_GROUP_SERVICE'],
    [ 'UNIVERSAL_USER_SERVICE', 'PERSON_SERVICE'],
    [ 'KNSServiceLocator\\.getKualiGroupService\\(\\)\\.getByGroupName\\(', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.getGroupByName\\(null, ' ],
    [ 'kualiGroupService\\(\\)\\.getByGroupName\\(', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.getGroupByName\\(null, ' ],
    [ 'KNSServiceLocator\\.getKualiGroupService\\(\\)\\.getUsersGroups\\((.*)\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.getPersonGroups\\(\$1, null\\)' ],
    [ 'kualiGroupService\\(\\)\\.getUsersGroups\\((.*)\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.getPersonGroups\\(\$1, null\\)' ],
    [ 'KNSServiceLocator\\.getKualiGroupService\\(\\)\\.groupExists\\(', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.groupExistsByName\\(null, ' ],
    [ 'kualiGroupService\\(\\)\\.groupExists\\(', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getIdentityManagementService\\(\\)\\.groupExistsByName\\(null, ' ],
    [ '\\.getGroupUsers\\(', '\\.getMembers\\(' ],    
    [ 'KualiGroup', 'KimGroup' ],
    [ 'kualiGroup', 'kimGroup' ],
    [ '\\.findPersons\\(', '\\.findPeople\\(' ], 
    // This one is ugly, but will need revisited anyway once we figure out what we are really going to do with supervisor access given KIM permissions/roles.
    [ 'user\\.isSupervisorUser\\(\\)', 'org\\.kuali\\.rice\\.kim\\.service\\.KIMServiceLocator\\.getPersonService\\(\\)\\.isMemberOfGroup\\(user, null, KNSServiceLocator\\.getKualiConfigurationService\\(\\)\\.getParameterValue\\(KNSConstants\\.KNS_NAMESPACE, KNSConstants.DetailTypes\\.DOCUMENT_DETAIL_TYPE, KNSConstants\\.CoreApcParms.SUPERVISOR_WORKGROUP\\)\\)' ], 
    [ '\"personUniversalIdentifier\"', '\"principalId\"' ],
    [ '\"personUserIdentifier\"', '\"principalName\"' ],
    [ '\"personName\"', '\"name\"' ],
    [ '\"personLocalPhoneNumbe\r\"', '\"phoneNumber\"' ],
    [ '\"personFirstName\"', '\"firstName\"' ],
    [ '\"personLastName\"', '\"lastName\"' ],
    [ '\"personEmailAddress\"', '\"emailAddress\"' ],
    [ '\"personCampusAddress\"', '\"addressLine1\"' ],
    [ '\"personBaseSalaryAmount\"', '\"baseSalaryAmount\"' ],
       
]

excludedDirectories = [
   '.svn',
   'CVS',
   'kim',
   'target',
   '.externalToolBuilders',
   '.settings'
]

backupExtension = ".backup"

createBackups = false;
deleteBackups = false;
testMode = false;
convertFiles = true;
deleteObseleteFiles = true;
restoreBackups = false;

def processDir( dir ) {
    println "Processing Directory: " + dir
    def files = new File(dir).list()
    files.each {
        String fileName ->
        if ( !excludedDirectories.contains( fileName ) && !fileName.endsWith( backupExtension ) ) {
            File file = new File(dir,fileName)
            //println "Processing File: " + file.getAbsolutePath()
            if ( file.isDirectory() ) {
                processDir( file.getAbsolutePath() )
            } else {
                String originalFileText = file.text
                String convertedFileText = originalFileText
                replacements.each {
                    fromStr, toStr -> 
                    //println "Converting: " + fromStr + " to " + toStr
                    convertedFileText = convertedFileText.replaceAll( fromStr, toStr )
                }
                if ( !convertedFileText.equals( originalFileText ) ) {
                    // if the file is about to be changed, backup the original
                    backupFile( file );
                    if ( !testMode ) {
                        file.delete();
                        file << convertedFileText;
                        file << "\n";
                        println "Changed File: " + file.getAbsolutePath();
                    } else {
                        println "Changed File Contents:"
                        println convertedFileText
                    }
                }
            }
        }
    }
}

def backupFile( File file ) {
    if ( createBackups ) {
        File backupFile = new File(file.getAbsolutePath() + backupExtension)
        if ( !backupFile.exists() ) {
            if ( !testMode ) {
                backupFile << file.text
                backupFile << "\n"
            } else {
                println "Would have backed up to: " + backupFile.getAbsolutePath()
            }
        }
    }
}

def deleteBackupsInDir( dir ) {
    println "Deleting Backups from Directory: " + dir
    def files = new File(dir).list()
    files.each {
        String fileName ->
        File backupFile = new File(dir,fileName)
        if ( !excludedDirectories.contains( fileName ) ) {
            if ( backupFile.isDirectory() ) {
                deleteBackupsInDir( backupFile.getAbsolutePath() )
            } else {
                if ( fileName.endsWith( backupExtension ) ) {
                    if ( !testMode ) {
                        backupFile.delete()
                    } else {
                        println "Would have deleted backup file: " + backupFile.getAbsolutePath()
                    }
                }
            }
        }
    }   
}

def restoreBackupsInDir( dir ) {
    println "Restoring Backups in Directory: " + dir
    def files = new File(dir).list()
    files.each {
        String fileName ->
        File backupFile = new File(dir,fileName)
        if ( !excludedDirectories.contains( fileName ) ) {
            if ( backupFile.isDirectory() ) {
                restoreBackupsInDir( backupFile.getAbsolutePath() )
            } else {
                if ( fileName.endsWith( backupExtension ) ) {
                    if ( !testMode ) {
                        File originalFile = new File( backupFile.getAbsolutePath().substring( 0, backupFile.getAbsolutePath().length() - 7 ) )
                        if ( originalFile.exists() ) {
                            originalFile.delete();
                        }
                        backupFile.renameTo( originalFile )
                        println "Restored: " + originalFile.getAbsolutePath()
                    } else {
                        println "Would have restored backup file: " + backupFile.getAbsolutePath()
                    }
                }
            }
        }
    }   
}

/** Initial tickoff of the processing **/
if ( convertFiles ) {   
    println "***** Converting Files *****"
    sourceDirectories.each {
        dir -> 
            processDir( baseDir + dir )
    }
}

if ( deleteObseleteFiles ) {
    println "******* Deleting specified files ******"
    filesToDelete.each {
        fileName ->
            File file = new File( baseDir + fileName )
            backupFile( file );
            println "Deleting File: " + file.getAbsolutePath();
            file.delete();
    }
}


if ( restoreBackups ) {
    println "***** Restoring Backup Files *****"
    sourceDirectories.each {
        dir ->
            restoreBackupsInDir( baseDir + dir )
    }
}

if ( deleteBackups ) {
    println "***** Deleting Backup Files *****"
    sourceDirectories.each {
        dir -> 
            deleteBackupsInDir( baseDir + dir )
    }
}
