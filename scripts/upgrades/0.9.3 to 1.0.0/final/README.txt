====
    Copyright 2005-2011 The Kuali Foundation

    Licensed under the Educational Community License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.opensource.org/licenses/ecl2.php

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
====

This file documents the steps required to upgrade a RICE 0.9.3 database to a RICE 1.0.0 database.


Step 1. Run the follow SQL scripts in this order:

kcb-upgrade
kew-upgrade - part 1 - for some reason the start of this file had updates that came before the refactor
package-refactor
kns-upgrade - part 1
refactor-db-names
ken-upgrade
kew-upgrade - part 2 - second part of the file
kns-upgrade - part 2

kim-create.sql - this will create the kim tables
kim_entity_import_from_kew.sql - this will copy existing data into the new KIM tables
kim_group_import.sql - will try to import group info into KIM
kim-data.sql - will populate kim with default data.  some inserts might fail if data already exists in kim.
update_final.sql - this is a compilation of all the daily sql updates minus KIM.  KIM is pulled new