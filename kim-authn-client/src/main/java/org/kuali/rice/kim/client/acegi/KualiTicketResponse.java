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

import java.util.List;

import org.acegisecurity.providers.cas.TicketResponse;


/**
 * Adds <code>AuthenticationSource</code> to the <code>TicketResponse</code>.
 *  
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KualiTicketResponse extends TicketResponse{
    //~ Instance fields ================================================================================================

    private String authenticationSource;

    //~ Constructors ===================================================================================================
  
/**
     * Constructor.
     * 
     * <P>
     * If <code>null</code> is passed into the <code>proxyList</code> or
     * <code>proxyGrantingTicketIou</code>, suitable defaults are established.
     * However, <code>null</code> cannot be passed for the <code>user</code>
     * or <code>authenticationSource</code>arguments.
     * </p>
     *
     * @param user the user as indicated by CAS (cannot be <code>null</code> or
     *        an empty <code>String</code>)
     * @param proxyList as provided by CAS (may be <code>null</code>)
     * @param proxyGrantingTicketIou as provided by CAS (may be
     *        <code>null</code>)
     * @param authenticationSource as provided by CAS (may be
     *        <code>null</code>)
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public KualiTicketResponse(String user, List proxyList, String proxyGrantingTicketIou, String authenticationSource) {
        super(user,proxyList,proxyGrantingTicketIou);

        if ((authenticationSource == null) || "".equals(authenticationSource)) {
            throw new IllegalArgumentException("Cannot pass null or empty String for AuthenticationSource");
        }
        
        this.authenticationSource = authenticationSource;
    }

    //~ Methods ========================================================================================================

   
    public String getAuthenticationSource() {
        return authenticationSource;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append("; Authentication Source: " + this.authenticationSource);

        return sb.toString();
    }
}
