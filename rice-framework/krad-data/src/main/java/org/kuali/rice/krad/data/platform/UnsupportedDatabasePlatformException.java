package org.kuali.rice.krad.data.platform;

/**
 * A runtime exception which indicates that an unsupported database platform was encountered.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UnsupportedDatabasePlatformException extends RuntimeException {

    public UnsupportedDatabasePlatformException() {}

    public UnsupportedDatabasePlatformException(DatabasePlatformInfo platformInfo) {
        super("Unsupported database platform " + platformInfo.toString());
    }

    public UnsupportedDatabasePlatformException(String message) {
        super(message);
    }

    public UnsupportedDatabasePlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedDatabasePlatformException(Throwable cause) {
        super(cause);
    }
}
