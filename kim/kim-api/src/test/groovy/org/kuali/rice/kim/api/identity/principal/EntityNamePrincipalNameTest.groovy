package org.kuali.rice.kim.api.identity.principal

import org.junit.Test
import org.kuali.rice.kim.api.test.JAXBAssert
import org.junit.Assert
import org.kuali.rice.kim.api.identity.name.EntityName
import org.kuali.rice.kim.api.identity.name.EntityNameTest

class EntityNamePrincipalNameTest {
    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_code_whitespace() {
        EntityNamePrincipalName entityNamePrincipalName = null;
        EntityNamePrincipalName.Builder builder = EntityNamePrincipalName.Builder.create(entityNamePrincipalName);
    }

    @Test
    void happy_path() {
        EntityName.Builder builder = new EntityName.Builder();
        EntityNamePrincipalName.Builder.create("ABC", builder);
    }
}
