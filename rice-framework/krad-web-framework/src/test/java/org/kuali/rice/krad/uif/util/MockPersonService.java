package org.kuali.rice.krad.uif.util;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;

/**
 * Mock person service implementation.
 */
class MockPersonService implements PersonService {

    /**
     * Get a mock person object for use in a JUnit test case.
     * 
     * @param id The ID to use for principal name, principal ID, and entity ID.
     * @return A mock person with the supplied ID.
     */
    public static Person getMockPerson(String id) {
        return new MockPerson(id);
    }

    @Override
    public Person getPerson(String principalId) {
        return getMockPerson(principalId);
    }

    @Override
    public List<Person> getPersonByExternalIdentifier(String externalIdentifierTypeCode,
            String externalId) {
        return null;
    }

    @Override
    public Person getPersonByPrincipalName(String principalName) {
        return getMockPerson(principalName);
    }

    @Override
    public Person getPersonByEmployeeId(String employeeId) {
        return null;
    }

    @Override
    public List<Person> findPeople(Map<String, String> criteria) {
        return null;
    }

    @Override
    public List<Person> findPeople(Map<String, String> criteria, boolean unbounded) {
        return null;
    }

    @Override
    public Class<? extends Person> getPersonImplementationClass() {
        return MockPerson.class;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Map<String, String> resolvePrincipalNamesToPrincipalIds(
            org.kuali.rice.krad.bo.BusinessObject businessObject,
            Map<String, String> fieldValues) {
        return null;
    }

    @Override
    public Person updatePersonIfNecessary(String sourcePrincipalId, Person currentPerson) {
        return null;
    }
}

