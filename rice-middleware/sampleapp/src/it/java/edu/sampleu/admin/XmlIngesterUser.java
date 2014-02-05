package edu.sampleu.admin;

public class XmlIngesterUser {

    public XmlIngesterUser(String id) {
        principalId = id;
        principalName = id;
        emplId = id;
        givenName = id.substring(0, 1);
        lastName = id.substring(1, id.length());
        emailAddress = id + "@bogus.email.com";
    }

    String principalId;
    String principalName;
    String emplId;
    String givenName;
    String lastName;
    String emailAddress;

    public String getPrincipalId() {
        return principalId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public String getEmplId() {
        return emplId;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
