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
update_final.sql - this is a compilation of all the daily sql updates minus KIM.  KIM is pulled new