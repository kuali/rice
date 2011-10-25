package org.kuali.rice.kim.impl.role;

import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.web.form.LookupForm;

import java.util.List;
import java.util.Map;

/**
 * Custom lookupable for the {@link RoleBo} lookup to call the role DAO for searching
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoleLookupableImpl extends LookupableImpl {
    private static final long serialVersionUID = -3149952849854425077L;

    @Override
    protected List<?> getSearchResults(LookupForm form, Map<String, String> searchCriteria, boolean unbounded) {
        List<RoleBo> roles = getRoleDao().getRoles(searchCriteria);

        return roles;
    }

    public RoleDao getRoleDao() {
        return KimImplServiceLocator.getRoleDao();
    }
}
