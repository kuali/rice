 Merge CX 2.3.6.x fixes into 2.5.2.x Notes
==========================================

Needs more attention
--------------------

* TODO: Store attachments in the database instead of file system. `59c9e51`
  * Has rice completed migration to JPA? If so, this change needs a revised fix.
  * Also see commit `2da0772`
* REVIEW: Adding callback handling and removal of save button on processed and final states. `ee550f8`
  * Double check the conflict resolution for: `rice-middleware/kns/src/main/java/org/kuali/rice/kns/web/struts/action/KualiDocumentActionBase.java`.
* REVIEW: Adding check for whether the document can actually be saved or not. `ccccbb3`
  * Double check the conflict resolution for: `rice-middleware/kns/src/main/java/org/kuali/rice/kns/web/struts/action/KualiDocumentActionBase.java`
