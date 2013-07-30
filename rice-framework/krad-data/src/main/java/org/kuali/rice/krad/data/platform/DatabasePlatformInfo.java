package org.kuali.rice.krad.data.platform;

/**
 * Contains database platform information, specifically the name and major version of the database. The name of the
 * database platform should be considered as case insensitive.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DatabasePlatformInfo {

    private final String name;
    private final int majorVersion;

    public DatabasePlatformInfo(String name, int majorVersion) {
        this.name = name;
        this.majorVersion = majorVersion;
    }

    public String getName() {
        return name;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DatabasePlatformInfo)) {
            return false;
        }

        final DatabasePlatformInfo that = (DatabasePlatformInfo) object;

        if (!name.equals(that.name)) {
            return false;
        }
        if (majorVersion != that.majorVersion) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + majorVersion;
        return result;
    }

    @Override
    public String toString() {
        return "DatabasePlatformInfo{" +
                "name='" + name + '\'' +
                ", majorVersion=" + majorVersion +
                '}';
    }
}
