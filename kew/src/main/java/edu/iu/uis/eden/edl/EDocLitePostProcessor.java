/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.edl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.ActionTakenEvent;
import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.DeleteEvent;
import edu.iu.uis.eden.postprocessor.DefaultPostProcessor;
import edu.iu.uis.eden.postprocessor.ProcessDocReport;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * PostProcessor responsible for posting events to a url defined in the EDL doc definition.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EDocLitePostProcessor extends DefaultPostProcessor {
    private static final Logger LOG = Logger.getLogger(EDocLitePostProcessor.class);
    private static final Timer TIMER = new Timer();

    private static String getURL(Document edlDoc) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return (String) xpath.evaluate("//edlContent/edl/eventNotificationURL", edlDoc, XPathConstants.STRING);
    }

    /**
     * @param urlstring
     * @param eventDoc
     */
    private static void submitURL(String urlstring, Document eventDoc) throws IOException, TransformerException {
        String content;
        try {
            content = XmlHelper.writeNode(eventDoc, true);
        } catch (TransformerException te) {
            LOG.error("Error writing serializing event doc: "+ eventDoc);
            throw te;
        }
        byte[] contentBytes = content.getBytes("UTF-8");

        LOG.debug("submitURL: " + urlstring);
        URL url = new URL(urlstring);

        String message = "POST " + url.getFile() + " HTTP/1.0\r\n" +
                         "Content-Length: " + contentBytes.length + "\r\n" +
                         "Cache-Control: no-cache\r\n" +
                         "Pragma: no-cache\r\n" +
                         "User-Agent: Java/1.4.2; EDocLitePostProcessor\r\n" +
                         "Host: " + url.getHost() + "\r\n" +
                         "Connection: close\r\n" +
                         "Content-Type: application/x-www-form-urlencoded\r\n\r\n" +
                         content;

        byte[] buf = message.getBytes("UTF-8");
        Socket s = new Socket(url.getHost(), url.getPort());

        /*URLConnection con = url.openConnection();
        LOG.debug("got connection: " + con);
        con.setDoOutput(true);
        con.setDoInput(true);
        LOG.debug("setDoOutput(true)");

        con.setRequestProperty("Connection", "close");
        con.setRequestProperty("Content-Length", String.valueOf(buf.length));*/

        OutputStream os = s.getOutputStream();
        try {
            try {
                os.write(buf, 0, buf.length);
                os.flush();
            } catch (InterruptedIOException ioe) {
                LOG.error("IO was interrupted while posting event to url " + urlstring + ": " + ioe.getMessage());
            } catch (IOException ioe) {
                LOG.error("Error posting EDocLite content to url " + urlstring + ioe.getMessage());
            } finally {
                try {
                    LOG.debug("Shutting down output stream");
                    s.shutdownOutput();
                } catch (IOException ioe) {
                    LOG.error("Error shutting down output stream for url " + urlstring + ": " + ioe.getMessage());
                }
            }

            InputStream is = s.getInputStream();
            try {

                buf = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // this is what actually forces the write on the URLConnection!
                int read = is.read(buf);
                if (read != -1) {
                    baos.write(buf, 0, read);
                }
                LOG.debug("EDocLite post processor response:\n" + new String(baos.toByteArray()));
            } catch (InterruptedIOException ioe) {
                LOG.error("IO was interrupted while reading response from url " + urlstring + ": " + ioe.getMessage());
            } catch (IOException ioe) {
                LOG.error("Error reading response from EDocLite handler url " + urlstring + ioe.getMessage());
            } finally {
                try {
                    LOG.debug("Shutting down input stream");
                    s.shutdownInput();
                } catch (IOException ioe) {
                    LOG.error("Error shutting down input stream for url " + urlstring + ": " + ioe.getMessage());
                }
            }
        } finally {
            try {
                s.close();
            } catch (IOException ioe) {
                LOG.error("Error closing socket", ioe);
            }
        }
    }

    protected static void postEvent(Long docId, Object event, String eventName) throws Exception {
        DocumentRouteHeaderValue val = KEWServiceLocator.getRouteHeaderService().getRouteHeader(docId);
        Document doc = getEDLContent(val);
        LOG.debug("Submitting doc: " + XmlHelper.jotNode(doc));

        String urlstring = getURL(doc);
        if (Utilities.isEmpty(urlstring)) {
            LOG.warn("No eventNotificationURL defined in EDLContent");
            return;
        }

        Document eventDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element eventE = eventDoc.createElement("event");
        eventE.setAttribute("type", eventName);
        eventDoc.appendChild(eventE);

        Element infoE = (Element) eventDoc.importNode(propertiesToXml(event, "info"), true);
        Element docIdE = eventDoc.createElement("docId");
        docIdE.appendChild(eventDoc.createTextNode(String.valueOf(docId)));
        infoE.appendChild(docIdE);

        eventE.appendChild(infoE);
        eventE.appendChild(eventDoc.importNode(doc.getDocumentElement(), true));

        String query = "docId=" + docId;
        if (urlstring.indexOf('?') != -1) {
            urlstring += "&" + query;
        } else {
            urlstring += "?" + query;
        }

        final String _urlstring = urlstring;
        final Document _eventDoc = eventDoc;
        // a super cheesy way to enforce asynchronicity/timeout follows:
        final Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    submitURL(_urlstring, _eventDoc);
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        });
        t.setDaemon(true);
        t.start();

        // kill the submission thread if it hasn't completed after 1 minute
        TIMER.schedule(new TimerTask() {
            public void run() {
                t.interrupt();
            }
        }, 60000);
    }

    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange event) throws Exception {
        LOG.debug("doRouteStatusChange: " + event);
//        postEvent(event.getRouteHeaderId(), event, "statusChange");
        return super.doRouteStatusChange(event);
    }

    public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
        LOG.debug("doActionTaken: " + event);
//        postEvent(event.getRouteHeaderId(), event, "actionTaken");
        return super.doActionTaken(event);
    }

    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
        LOG.debug("doDeleteRouteHeader: " + event);
//        postEvent(event.getRouteHeaderId(), event, "deleteRouteHeader");
        return super.doDeleteRouteHeader(event);
    }

    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange event) throws Exception {
        LOG.debug("doRouteLevelChange: " + event);
//        postEvent(event.getRouteHeaderId(), event, "routeLevelChange");
        return super.doRouteLevelChange(event);
    }

    public static Document getEDLContent(DocumentRouteHeaderValue routeHeader) throws Exception {
        String content = routeHeader.getDocContent();
        Document doc =  DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(content)));
        return doc;
    }

    public static DocumentBuilder getDocumentBuilder() throws Exception {
    	return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    private static String lowerCaseFirstChar(String s) {
        if (s.length() == 0 || Character.isLowerCase(s.charAt(0))) return s;
        StringBuffer sb = new StringBuffer(s.length());
        sb.append(Character.toLowerCase(s.charAt(0)));
        if (s.length() > 1) {
            sb.append(s.substring(1));
        }
        return sb.toString();
    }


    public static Element propertiesToXml(Object o, String elementName) throws Exception {
        Class c = o.getClass();
        Document doc = getDocumentBuilder().newDocument();
        Element wrapper = doc.createElement(elementName);
        Method[] methods = c.getMethods();
        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getName();
            if ("getClass".equals(name)) continue;
            if (!name.startsWith("get") ||
                methods[i].getParameterTypes().length > 0) continue;
            name = name.substring("get".length());
            name = lowerCaseFirstChar(name);
            String value = null;
            try {
                Object result = methods[i].invoke(o, null);
                if (result == null) {
                    LOG.debug("value of " + name + " method on object " + o.getClass() + " is null");
                    value = "";
                } else {
                    value = result.toString();
                }
                Element fieldE = doc.createElement(name);
                fieldE.appendChild(doc.createTextNode(value));
                wrapper.appendChild(fieldE);
            } catch (RuntimeException e) {
                LOG.error("Error accessing method '" + methods[i].getName() + " of instance of " + c);
                throw e;
            } catch (Exception e) {
                LOG.error("Error accessing method '" + methods[i].getName() + " of instance of " + c);
            }
        }
        return wrapper;
    }
}