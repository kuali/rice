/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.rice.kim.client.acegi;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Populates a UserDetails object with username and Authentication Method
 *  
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KualiUserDetailsServiceImpl implements KualiUserDetailsService, InitializingBean
{
    private static final Log logger = LogFactory.getLog(KualiUserDetailsServiceImpl.class);

    public void afterPropertiesSet() throws Exception {}
    
    public UserDetails loadUserByTicketResponse(KualiTicketResponse response) {
        GrantedAuthority[] authorities = new GrantedAuthority[1];
        authorities[0]= new GrantedAuthorityImpl("ROLE_" + response.getAuthenticationSource());
              
        return loadUserByUsernameAndAuthorities(response.getUser(), authorities); 
    }

    public UserDetails loadUserByUsername(String username)
    {
        return loadUserByUsernameAndAuthorities(username, new GrantedAuthority[0]);        
    }
    
    public UserDetails loadUserByUsernameAndAuthorities(String username, GrantedAuthority[] authorities) {
        GrantedAuthority[] newAuthorities = new GrantedAuthority[authorities.length+1];
        newAuthorities[authorities.length]= new GrantedAuthorityImpl("ROLE_KUALI_USER");
        logger.warn("setting granted authorities:" + newAuthorities.toString());
        UserDetails user = new User(username, "empty_password", true, true, true, true, newAuthorities);    
        return user;
    }

   
}