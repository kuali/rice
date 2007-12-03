/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.test.web.framework;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.test.web.framework.actions.AssertAction;
import edu.iu.uis.eden.test.web.framework.actions.EchoAction;
import edu.iu.uis.eden.test.web.framework.actions.ParametersAction;
import edu.iu.uis.eden.test.web.framework.actions.SleepAction;
import edu.iu.uis.eden.test.web.framework.actions.SubmitAction;
import edu.iu.uis.eden.test.web.framework.actions.UserAction;
import edu.iu.uis.eden.test.web.framework.actions.VariableAction;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * A web site interaction script.
 * In general, where it makes sense, action attributes are inspected according to the
 * algorithm defined in {@link edu.iu.uis.eden.test.web.framework.Util#getResolvableAttribute(Node, String, PropertyScheme)}
 * <pre>
 * &lt;script&gt;
 * ... script actions ...
 * &lt;/script&gt;
 * </pre>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Script {
    private static final Logger LOG = Logger.getLogger(Script.class);

    private static final Class[] SCRIPT_ACTIONS = { AssertAction.class, 
                                                    EchoAction.class,
                                                    ParametersAction.class,
                                                    SleepAction.class,
                                                    SubmitAction.class,
                                                    UserAction.class,
                                                    VariableAction.class
                                                  };

    private static final Map ACTION_MAP = new HashMap();
    static {
        for (int i = 0; i < SCRIPT_ACTIONS.length; i++) {
            Class c = SCRIPT_ACTIONS[i];
            ScriptAction action;
            try {
                action = (ScriptAction) c.newInstance();
            } catch (IllegalAccessException iae) {
                throw new RuntimeException("Error loading ScriptAction: " + c, iae);
            } catch (InstantiationException ie) {
                throw new RuntimeException("Error loading ScriptAction: " + c, ie);
            }
            String[] names = action.getNames();
            for (int j = 0; j < names.length; j++) {
                ACTION_MAP.put(names[j], action);
            }
        }
    }

    private static final InteractionController MOCK_INTERACTION_CONTROLLER = new InteractionController() {
        public String submit(String method, String uri, Script script) throws Exception {
            return null;
        }
    };

    private Document script;
    private InteractionController controller;
    private ScriptState state = new ScriptState();

    public Script(String script) throws IOException, ParserConfigurationException, SAXException {
      this(script, MOCK_INTERACTION_CONTROLLER);
    }

    public Script(String script, InteractionController controller) throws IOException, ParserConfigurationException, SAXException {
      this(new ByteArrayInputStream(script.getBytes()), controller);
    }

    public Script(InputStream script) throws IOException, ParserConfigurationException, SAXException {
        this(script, MOCK_INTERACTION_CONTROLLER);
    }

    public Script(InputStream script, InteractionController controller) throws IOException, ParserConfigurationException, SAXException {
        this(new InputSource(script), controller);
    }

    public Script(InputSource script) throws IOException, ParserConfigurationException, SAXException {
        this(script, MOCK_INTERACTION_CONTROLLER);
    }

    public Script(Document doc) {
        this(doc, MOCK_INTERACTION_CONTROLLER);
    }

    public Script(InputSource script, InteractionController controller) throws IOException, ParserConfigurationException, SAXException {
        this(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(script), controller);
    }

    public Script(Document script, InteractionController controller) {
        this.script = script;
        this.controller = controller;
    }

    public InteractionController getController() {
        return controller;
    }

    public ScriptState getState() {
        return state;
    }

    public void run(Map context) {
        // init standard script variables
        state.reset();
        state.setContext(context);

        NodeList list = script.getDocumentElement().getChildNodes();

        Node node = null;
        for (int i = 0; i < list.getLength(); i++) {
            node = list.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
    
            String name = node.getNodeName();
            LOG.debug("Processing: " + name);

            ScriptAction action = (ScriptAction) ACTION_MAP.get(name);
            if (action == null) {
                LOG.error("Invalid action: '" + name + "'");
                return;
            }
            try {
                action.process(this, node);
            } catch (RuntimeException e) {
                LOG.error("Exception occurred at node: " + XmlHelper.jotNode(node));
                throw e;
            } catch (Error e) {
                LOG.error("Error occurred at node: " + XmlHelper.jotNode(node));
                throw e;
            }
        }
    }
}