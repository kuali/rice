package org.kuali.rice.krad.uif.util;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.DataObjectAuthorizationService;

public class MockDataObjectAuthorizationService implements DataObjectAuthorizationService {

    @Override
    public boolean attributeValueNeedsToBeEncryptedOnFormsAndLinks(Class<?> dataObjectClass, String attributeName) {
        return false;
    }

    @Override
    public boolean canCreate(Class<?> dataObjectClass, Person user, String docTypeName) {
        return false;
    }

    @Override
    public boolean canMaintain(Object dataObject, Person user, String docTypeName) {
        return false;
    }

}
