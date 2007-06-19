package org.kuali.rice.test;

import org.kuali.rice.lifecycle.Lifecycle;
import org.mortbay.jetty.Server;

/**
 * Abstract class to provide convenience methods for starting and stopping a
 * Jetty Server instance.
 * 
 * @author
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
 * @since 0.9
 */
public abstract class BaseTestServer implements Lifecycle {

    private Server server;

    protected abstract Server createServer();

    public Server getServer() {
        return this.server;
    }

    public void start() throws Exception {
        this.server = createServer();
        this.server.start();
    }

    public void stop() throws Exception {
        this.server.stop();
    }

    public boolean isStarted() {
        return this.server.isStarted();
    }
}
