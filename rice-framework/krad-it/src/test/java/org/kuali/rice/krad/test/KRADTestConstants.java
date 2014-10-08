/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.test;

/**
 * Provides centralized storage of constants that occur throughout the tests
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class KRADTestConstants {

    public static final String TEST_NAMESPACE_CODE = "TEST";

    public static final class TestConstants {

        private static final String HOST = "localhost";
        private static final String PORT = "8080";
        public static final String BASE_PATH = "http://" + HOST + ":" + PORT + "/";
        public static final String MESSAGE =
                "JUNIT test entry. If this exist after the tests are not cleaning up correctly. Created by class";

        private TestConstants() {
            throw new UnsupportedOperationException("do not call");
        }
    }

    public static final class DataAttributesConstants {

        public static final String PARENT = "parent";
    }

    public static final class CssClassNames {

        public static final String INPUT_FIELD = "uif-inputField";
        public static final String HORIZONTAL_FIELD_GROUP = "uif-horizontalFieldGroup";
        public static final String ACTION_COLUMN = "uif-collection-column-action";
        public static final String ADD_LINE_ROW = "uif-collectionAddItem";
        public static final String MODAL_DIALOG_BODY = "modal-body";
        public static final String MODAL_DIALOG_FOOTER = "modal-footer";
        public static final String MODAL_DIALOG_HEADER = "modal-header";
        public static final String ROW_DETAILS_GROUP = "uif-rowDetails";
        public static final String SUB_COLLECTION = "uif-subCollection";
    }

    private KRADTestConstants() {
        throw new UnsupportedOperationException("do not call");
    }
}
