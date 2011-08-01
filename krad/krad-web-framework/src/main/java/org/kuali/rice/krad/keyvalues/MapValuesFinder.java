package org.kuali.rice.krad.keyvalues;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** a simple values finder that uses a map as the key/value source. */
public class MapValuesFinder implements KeyValuesFinder {

    private Map<String, String> keyValues;

    public MapValuesFinder(Map<String, String> keyValues) {
        setKeyValues(keyValues);
    }

    public void setKeyValues(Map<String, String> keyValues) {
        if (keyValues == null) {
            throw new IllegalArgumentException("keyValues was null");
        }
        this.keyValues = Collections.unmodifiableMap(new HashMap<String, String>(keyValues));
    }

    @Override
    public List<KeyValue> getKeyValues() {
        final List<KeyValue> list = new ArrayList<KeyValue>();
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            list.add(new ConcreteKeyValue(entry));
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<KeyValue> getKeyValues(boolean includeActiveOnly) {
        return getKeyValues();
    }

    @Override
    public Map<String, String> getKeyLabelMap() {
        return keyValues;
    }

    @Override
    public String getKeyLabel(String key) {
        return keyValues.get(key);
    }

    @Override
    public void clearInternalCache() {
        //do nothing
    }
}
