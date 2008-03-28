package org.kuali.rice.kim.client.acegi;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.ldap.LdapUserSearch;
import org.acegisecurity.providers.ldap.LdapAuthoritiesPopulator;
import org.acegisecurity.userdetails.User;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class LdapUserDetailsService implements UserDetailsService, InitializingBean
{
    LdapUserSearch           ldapUserSearch;
    LdapAuthoritiesPopulator ldapAuthoritiesPopulator;

    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(this.ldapUserSearch, "An LDAP search object must be set");
        Assert.notNull(this.ldapAuthoritiesPopulator, "An LDAP authorities populator must be set");
    }

    public UserDetails loadUserByUsername(String username)
    {
        LdapUserDetails ldapUserDetails = ldapUserSearch.searchForUser(username);
        GrantedAuthority[] authorities = ldapAuthoritiesPopulator.getGrantedAuthorities(ldapUserDetails);

        return new User(username, "empty_password", true, true, true, true, authorities);
    }

    public LdapAuthoritiesPopulator getLdapAuthoritiesPopulator()
    {
        return ldapAuthoritiesPopulator;
    }

    public void setLdapAuthoritiesPopulator(LdapAuthoritiesPopulator ldapAuthoritiesPopulator)
    {
        this.ldapAuthoritiesPopulator = ldapAuthoritiesPopulator;
    }

    public LdapUserSearch getLdapUserSearch()
    {
        return ldapUserSearch;
    }

    public void setLdapUserSearch(LdapUserSearch ldapUserSearch)
    {
        this.ldapUserSearch = ldapUserSearch;
    }
}