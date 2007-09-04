Overview

These schemas are read from the classloader (for now, "schema/" + name + ".xsd") from 
the XmlIngesterServiceImpl via a ClassLoaderEntityResolver.
Validation can be turned off in the XmlIngesterServiceImpl through the 'validation' 
property via Spring.

Schemas:

common.xsd - defines a few simple string types that reused throughout the other schemas


NotificationMessage.xsd - Schema for notifications.

ContentSimple.xsd - Schema for defining simple message content

ContentEvent.xsd - Schema for defining a generic event message

NotificationResponse.xsd - Schema for defining a notification response



XMLSchema.xsd - I downloaded this XML Schema Schema (boggles...) because it 
appeared that at one point it was inaccessible on the web and the xml parser 
was dying.  Not used since, but it's probably a good idea to keep around.  We 
may want to just use this local copy in the future to prevent everything 
crumbling if the intar-web goes down.

xml.xsd - 

DTDs:

datatypes.dtd - 
XMLSchema.dtd -
