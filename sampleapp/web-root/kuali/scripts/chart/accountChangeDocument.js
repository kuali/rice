/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function loadAccountName( acctField ) {
    var elPrefix = findElPrefix( acctField.name );
	var coaCode = getElementValue( elPrefix + ".chartOfAccountsCode" );
	var accountCode = getElementValue( acctField.name );
	var nameFieldName = elPrefix + ".accountName";
	
	if ( accountCode != "" && coaCode != "" ) {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( nameFieldName, data.accountName );
			} else {
				setRecipientValue( nameFieldName, wrapError( "account not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( nameFieldName, wrapError( "account not found" ), true );
			}
		};
		AccountService.getByPrimaryIdWithCaching( coaCode, accountCode, dwrReply );
	} else {
		setRecipientValue( nameFieldName, "" );
	}
}
