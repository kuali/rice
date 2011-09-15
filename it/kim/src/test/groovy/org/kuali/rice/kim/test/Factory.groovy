package org.kuali.rice.kim.test

import org.kuali.rice.core.api.mo.common.Identifiable

/**
 * Helps constructs objects
 */
class Factory {
    def private static factories = []
    def private static long counter = System.currentTimeMillis()

    def Factory() {
        factories.add(this)
    }

    /**
     * returns string ids using a global in memory counter
     * @return
     */
    def synchronized static makeId() {
        counter++;
        Long.toHexString(counter)
    }

    /**
     * Invoke a factory method
     * @param fields object field values
     * @param type name of type/factory method
     * @return constructed object
     */
    def static make(Map fields = [:], type) {
        def short_name = { name ->
            (name =~ /([^\.]*)$/)[0][1]
        }
        def methodName = short_name(type.name)
        def found = factories.findResult {
            def method = it.metaClass.methods.find { it.name == methodName }
            if (method != null) {
                return it."$methodName"(fields)
            }
        }
        if (found != null) {
            return found
        } else {
            throw new RuntimeException("No factory found for " + type)
        }
    }

    /**
     * Determines appropriate key for association and inserts it into the map of field values
     * @param key associated object field name
     * @param fields object field values
     * @param defaults default/generated values
     * @param id_key optional foreign key field name, if ommitted: key + 'Id'
     * @return new field value map
     */
    def static assignRelationId(key, fields, defaults = [:], id_key = key + 'Id') {
        Identifiable identifiable = fields[key]
        if (identifiable != null) {
            println("Setting $id_key to $identifiable.id")
            defaults[id_key] = identifiable.id
        }
        defaults.putAll(fields)
        defaults.remove(key)
        defaults
    }
}
