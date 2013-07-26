package org.kuali.rice.krad.data.platform.generator;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;

import java.sql.Connection;

/**
 * IdGenerator implementation which defers to the Rice {@link DatabasePlatform} which selects
 * an appropriate backend implementation for the underlying database.
 * This IdGenerator should be configured via {@link Sequence}
 * annotations on entity classes.
 */
public class DatabasePlatformIdGenerator implements IdGenerator {
    private static final Logger LOG = Logger.getLogger(DatabasePlatformIdGenerator.class);

    /**
     * The logical "sequence" name
     */
    private String sequenceName;

    /**
     * Construct the DatabasePlatformIdGenerator with a logical "sequence" name
     * @param sequenceName
     */
    public DatabasePlatformIdGenerator(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    @Override
    public String getName() {
        // for now name is just the sequence name
        // this implies DatabasePlatformIdGenerators based on the
        // same sequence are interchangeable (which is the case)
        return sequenceName;
    }

    @Override
    public Object getNextValue(Connection connection) {
        Long value = -1L;
        try {
            DatabasePlatform platform = (DatabasePlatform) GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
            value = platform.getNextValSQL(this.sequenceName, connection);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return value;
    }
}