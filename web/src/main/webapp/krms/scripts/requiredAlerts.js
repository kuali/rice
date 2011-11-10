/*
 * Copyright 2005-2011 The Kuali Foundation
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
function requiredsSet() {
    // 178_attribute is Namespace
    if (document.getElementById('178_attribute').selected == '') {
      alert('Please select a Namespace');
      return false;
    }
    // 204 is Agenda Name
    if (document.getElementById('204').value == '') {
      alert('Please enter the Agenda Name');
      return false;
    }
    // 230 is Agenda Context
    if (document.getElementById('230').value == '') {
      alert('Please Lookup (click the magnifying glass icon) the Agenda Context');
      return false;
    }
    return true;
}

