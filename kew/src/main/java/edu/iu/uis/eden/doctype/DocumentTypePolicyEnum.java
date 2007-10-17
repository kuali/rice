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
package edu.iu.uis.eden.doctype;

/**
 * DocumentType policy enum type.
 * Encapsulates  policies of the document.
 * There are 4 policies 'DEFAULT_APPROVE', 'INITIATOR_MUST_ROUTE' and 'LOOK_FUTURE'.
 * FIXME:: fix the docs on this... are there 3, 4 or 5 policies?  what about PRE_APPROVE and DISAPPROVE?
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public final class DocumentTypePolicyEnum {
    /**
     * FIXME: needs docs
     */
    public static final DocumentTypePolicyEnum PRE_APPROVE = new DocumentTypePolicyEnum("PRE_APPROVE");
    /**
     * FIXME: needs docs
     */
    public static final DocumentTypePolicyEnum DISAPPROVE = new DocumentTypePolicyEnum("DISAPPROVE");

    /**
     * determines whether a document will go processed without any approval requests.  If
     * a document has this policy set to false and doesn't generate and approval requests the document will
     * be put in exception routing, which is the exception workgroup associated with the last route node or the
     * workgroup defined in the 'defaultExceptionWorkgroupName'.  This policy if not defined in this or a parent
     * document type defaults to true
     */
    public static final DocumentTypePolicyEnum DEFAULT_APPROVE = new DocumentTypePolicyEnum("DEFAULT_APPROVE");
    /**
     * determines if the user that initiated a document must 'route' the document when it is
     * in the initiated state.  Defaults to true.
     */
    public static final DocumentTypePolicyEnum INITIATOR_MUST_ROUTE = new DocumentTypePolicyEnum("INITIATOR_MUST_ROUTE");
    /**
     * determines if the user that initiated a document must 'route' the document when it is
     * in the initiated state.  Defaults to true.
     */
    public static final DocumentTypePolicyEnum INITIATOR_MUST_SAVE = new DocumentTypePolicyEnum("INITIATOR_MUST_SAVE");
    public static final DocumentTypePolicyEnum INITIATOR_MUST_CANCEL = new DocumentTypePolicyEnum("INITIATOR_MUST_CANCEL");
    public static final DocumentTypePolicyEnum INITIATOR_MUST_BLANKET_APPROVE = new DocumentTypePolicyEnum("INITIATOR_MUST_BLANKET_APPROVE");

    /**
     * determines whether the document can be brought into a simulated route from the route log.  A
     * simulation of where the document would end up if it where routed to completion now.  Defaults to false.
     */
    // determines if route log will show the look into the future link
    public static final DocumentTypePolicyEnum LOOK_FUTURE = new DocumentTypePolicyEnum("LOOK_FUTURE");

    public static final DocumentTypePolicyEnum SEND_NOTIFICATION_ON_SU_APPROVE = new DocumentTypePolicyEnum("SEND_NOTIFICATION_ON_SU_APPROVE");

    public static final DocumentTypePolicyEnum SUPPORTS_QUICK_INITIATE = new DocumentTypePolicyEnum("SUPPORTS_QUICK_INITIATE");

    public static final DocumentTypePolicyEnum NOTIFY_ON_SAVE = new DocumentTypePolicyEnum("NOTIFY_ON_SAVE");

    private final String name;

    public DocumentTypePolicyEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "[DocumentTypePolicyEnum: name=" + name + "]";
    }

    public static DocumentTypePolicyEnum lookup(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Document type policy name must be non-null");
        }
        if (DISAPPROVE.name.equalsIgnoreCase(name)) {
            return DISAPPROVE;
        } else if (DEFAULT_APPROVE.name.equalsIgnoreCase(name)) {
            return DEFAULT_APPROVE;
        } else if (PRE_APPROVE.name.equalsIgnoreCase(name)) {
            return PRE_APPROVE;
        } else if (INITIATOR_MUST_ROUTE.name.equalsIgnoreCase(name)) {
            return INITIATOR_MUST_ROUTE;
        } else if (INITIATOR_MUST_SAVE.name.equalsIgnoreCase(name)) {
            return INITIATOR_MUST_SAVE;
        } else if (INITIATOR_MUST_BLANKET_APPROVE.name.equalsIgnoreCase(name)) {
            return INITIATOR_MUST_BLANKET_APPROVE;
        } else if (INITIATOR_MUST_CANCEL.name.equalsIgnoreCase(name)) {
            return INITIATOR_MUST_CANCEL;
        } else if (LOOK_FUTURE.name.equalsIgnoreCase(name)) {
            return LOOK_FUTURE;
        } else if (SEND_NOTIFICATION_ON_SU_APPROVE.name.equalsIgnoreCase(name)) {
        	return SEND_NOTIFICATION_ON_SU_APPROVE;
        } else if (SUPPORTS_QUICK_INITIATE.name.equalsIgnoreCase(name)) {
        	return SUPPORTS_QUICK_INITIATE;
        } else if (NOTIFY_ON_SAVE.name.equalsIgnoreCase(name)) {
        	return NOTIFY_ON_SAVE;
        } else {
            throw new IllegalArgumentException("Invalid Document type policy: '" + name + "'");
        }
    }
}