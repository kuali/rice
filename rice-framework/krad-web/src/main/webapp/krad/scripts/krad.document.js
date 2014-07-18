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

// KULRICE-7339. A patch to disable validation on all required fields, except document header description,
// when the Action Requested select control for Ad Hoc Group Requests has a value of COMPLETE.

function isRouteToComplete(kradRequest) {

    // For this case, validation comes in false by default
    kradRequest.validate = true;
    var showForm = false ;

    // special case for Action Requested select control
    jQuery("select[name^='document.adHocRouteWorkgroups['],[name^='document.adHocRoutePersons[']").each(function isSetToComplete(index, selectControl) {
        if (jQuery(selectControl).val() === "C") {
            kradRequest.validate = false;

            // document description must still be filled in, or do not show form
            showForm = validateFieldValue(jQuery("input[name='document.documentHeader.documentDescription']"));
        }
    });

    if (kradRequest.validate) {
        showForm = kradRequest._validateBeforeAction();
    }

    return showForm;
}
