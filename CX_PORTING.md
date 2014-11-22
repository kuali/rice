 Merge CX 2.3.6.x fixes into 2.5.2.x Notes
==========================================

Needs more attention
--------------------

* Store attachments in the database instead of file system. `59c9e513e008c27073b7a8ae73ed99c4915d303b`
  * Has rice completed migration to JPA? If so, this change needs a revised fix.
  * Also see commit `2da0772c5c947cf16364f08149dcc1bbf2dd6374`
* Adding callback handling and removal of save button on processed and final states. `ee550f8e628888ee2961314f88c5c545aef65576`
  * Double check the merge resolution for: `rice-middleware/kns/src/main/java/org/kuali/rice/kns/web/struts/action/KualiDocumentActionBase.java`.
