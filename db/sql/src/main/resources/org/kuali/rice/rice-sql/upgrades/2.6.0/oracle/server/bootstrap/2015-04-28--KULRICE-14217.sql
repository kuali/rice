--
-- KULRICE-14217 changes to allow for updating an external action list.  NOTE:  This is for Oracle only.
--

--
--  !!! IMPORTANT !!!    The following grants must be run as SYS
--

--   GRANT CONNECT, RESOURCE TO RICE
--   /
--   GRANT EXECUTE ANY PROCEDURE TO RICE
--   /
--   GRANT aq_administrator_role TO RICE
--   /
--   GRANT aq_user_role TO RICE
--   /
--   GRANT EXECUTE ON dbms_aqadm TO RICE
--   /
--   GRANT EXECUTE ON dbms_aq TO RICE
--   /
--   GRANT EXECUTE ON dbms_aqin TO RICE
--   /
--   GRANT EXECUTE ON dbms_aqjms to RICE
--   /


--
--  !!! IMPORTANT !!!    The grants above must be run once before the statements below will successfully run.
--


--
-- Drop the queue and message type if they already exist.
--
DECLARE temp NUMBER;
BEGIN
  SELECT COUNT(*) INTO temp FROM ALL_QUEUES where name = 'ACTN_ITEM_CHANGED_MQ';
  IF temp > 0 THEN
    DBMS_AQADM.DROP_QUEUE_TABLE(
      queue_table        => 'actn_item_changed_mq_t',
      force              => true);
  END IF;
END;
/

DECLARE temp NUMBER;
BEGIN
SELECT COUNT(*) INTO temp FROM ALL_TYPES  WHERE TYPE_NAME = 'ACTN_ITEM_CHANGED_MESSAGE_TYP';
  IF temp > 0 THEN
    EXECUTE IMMEDIATE 'DROP type actn_item_changed_message_typ';
  END IF;
END;
/

--
-- Create a message type
--
CREATE type actn_item_changed_message_typ as object (
    actn_type        VARCHAR2(1),
    actn_item_id     VARCHAR2(40)
)
/

--
-- Create the queue table
--
BEGIN
	DBMS_AQADM.CREATE_QUEUE_TABLE (
		queue_table        => 'actn_item_changed_mq_t',
		sort_list 		     => 'PRIORITY,ENQ_TIME',
		queue_payload_type => 'actn_item_changed_message_typ'
	);
END;
/

--
--  Create the queue and start up the queue
--
BEGIN
	DBMS_AQADM.CREATE_QUEUE (
		queue_name  => 'actn_item_changed_mq',
		queue_table => 'actn_item_changed_mq_t'
    );

	DBMS_AQADM.START_QUEUE (
		queue_name => 'actn_item_changed_mq'
    );
END;
/


--
--  Create the trigger that will call the enqueue_actn_item procedure
--
CREATE OR REPLACE TRIGGER ACTN_ITEM_CHANGED
AFTER INSERT OR UPDATE OR DELETE ON KREW_ACTN_ITM_T
  FOR EACH ROW
  BEGIN
    -- Use 'I' for an INSERT, 'U' for UPDATE, and 'D' for DELETE
	  IF INSERTING THEN
		  enqueue_actn_item('I', :new.actn_itm_id);
	  ELSIF UPDATING THEN
	    enqueue_actn_item('U', :new.actn_itm_id);
	  ELSE
		  enqueue_actn_item('D', :old.actn_itm_id);
	  END IF;
  END;
/

--
-- Create the procedure that will create a message in the actn_item_changed_mq
--
CREATE OR REPLACE PROCEDURE enqueue_actn_item(actionType varchar2, actn_itm_id krew_actn_itm_t.actn_itm_id%TYPE, prio number DEFAULT 4)
AS
message actn_item_changed_message_typ := actn_item_changed_message_typ(actionType, actn_itm_id);
enqueue_options dbms_aq.enqueue_options_t;
message_properties dbms_aq.message_properties_t;
message_handle RAW(16);
BEGIN
	message_properties.priority := prio;

	dbms_aq.enqueue(queue_name => 'actn_item_changed_mq',
		enqueue_options      => enqueue_options,
		message_properties   => message_properties,
		payload              => message,
		msgid                => message_handle);
END;
/