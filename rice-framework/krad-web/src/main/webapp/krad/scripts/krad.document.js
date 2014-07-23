/*
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
 /*
 * Copyright 2006-2014 The Kuali Foundation
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

/**
 * KULRICE-7339. A patch to disable validation on all required fields, except document header description,
 * when the Action Requested select control for Ad Hoc Recipients has a value of COMPLETE.
 *
 * @returns {boolean} true if all fields requiring validation are valid, false otherwise
 */
function isRouteToComplete(kradRequest) {

    var $fieldsToSkip = jQuery(".required").not("input[name='document.documentHeader.documentDescription']");
    var valid = validatePartialForm($fieldsToSkip, isAdHocComplete);

    if (!valid) {
        clearHiddens();
    }

    return valid;
}

/**
 * Determines if the Action Requested select control for Ad Hoc Recipients has a value of COMPLETE.
 *
 * @returns {boolean} true if the above condition exists, false otherwise.
 */
function isAdHocComplete() {

    var adHocRoutes = jQuery("select[name^='document.adHocRouteWorkgroups['],[name^='document.adHocRoutePersons[']");
    var complete = false;

    adHocRoutes.each(function isSetToComplete(index, selectControl) {
        if (jQuery(selectControl).val() === "C") {
            complete = true;
            return false;
        }
    });

    return complete;
}
