/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.notification.util;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.deployer.WebAppDeployer;

/**
 * Starts up Jetty pointing to an existing installation location.
 * This is useful for testing/debugging.  To use this class (with the default
 * webapp dir location), generate a Quickstart in dist/ before running
 * this class.
 * Implementation derived from this discussion: http://www.nabble.com/Re:-Embedded-Jetty6-with-AXIS2-p8960637.html
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationServer {
    public static void main(String[] args) throws Exception {
        // default to quickstart location
        String webappDir = "./dist/quickstart/webapps";
        if (args.length > 0) {
            webappDir = args[0];
        }
        int port = 8080;
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }

        Server server = new Server(port);

        WebAppDeployer webAppDeployer = new WebAppDeployer();
        webAppDeployer.setContexts(server);
        webAppDeployer.setWebAppDir(webappDir);
        webAppDeployer.setParentLoaderPriority(false); 
        webAppDeployer.setExtract(true);

        server.addLifeCycle(webAppDeployer);

        server.setStopAtShutdown(true);
        server.setSendServerVersion(true); 

        server.start();
        server.join();
    }
}
