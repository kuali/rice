package org.kuali.rice.krad.uif.authorization;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewAuthorizer {

    public Set<String> getActionFlags(UifFormBase model, Person user, Set<String> actions);

    public Set<String> getEditModes(UifFormBase model, Person user, Set<String> editModes);

}
