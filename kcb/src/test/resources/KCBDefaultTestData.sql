insert into KCB_MESSAGES (
    ID,
    DELIVERY_TYPE,
    TITLE,
    CHANNEL,
    PRODUCER,
    CONTENT,
    CONTENT_TYPE,
    USER_RECIPIENT_ID
) values (
    1,
    'ack',
    'title1',
    'channel1',
    'producer1',
    'content1',
    NULL,
    'user1'
) /

insert into KCB_MSG_DELIVS (
    ID,
    MESSAGE_ID,
    DELIVERER_TYPE_NAME,
    DELIVERER_SYSTEM_ID,
    DELIVERY_STATUS
) values {
    1,
    1,
    'mock',
    NULL,
    '-'
) /

insert into KCB_RECIP_PREFS (
    ID,
    RECIPIENT_ID,
    PROPERTY,
    VALUE
) values (
    1,
    'user1',
    'property1',
    'value1'
) /

insert into KCB_RECIP_DELIVS (
    ID,
    RECIPIENT_ID,
    CHANNEL,
    DELIVERER_NAME,
) values (
    1,
    'user1',
    'channel1',
    'mock'
) /
   