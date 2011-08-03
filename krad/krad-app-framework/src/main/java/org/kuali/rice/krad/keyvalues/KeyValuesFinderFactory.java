package org.kuali.rice.krad.keyvalues;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/** a factory for creating key-values finders. */
public final class KeyValuesFinderFactory {
    private KeyValuesFinderFactory() {
        throw new UnsupportedOperationException("do not call");
    }

    public static KeyValuesFinder fromMap(Map<String, String> map) {
        if (map == null) {
            throw new IllegalArgumentException("map is null");
        }

        return new MapBased(map);
    }

    private static final class MapBased implements KeyValuesFinder {
        private final Map<String, String> map;

        private MapBased(Map<String, String> map) {
            this.map = ImmutableMap.copyOf(map);
        }

        @Override
        public List<KeyValue> getKeyValues() {
            Collection<KeyValue> kvs = Collections2.transform(map.entrySet(), new Function<Map.Entry<String, String>, KeyValue>() {
                @Override
                public KeyValue apply(Map.Entry<String, String> input) {
                    return new ConcreteKeyValue(input);
                }
            });
            return ImmutableList.copyOf(kvs);
        }

        @Override
        public List<KeyValue> getKeyValues(boolean includeActiveOnly) {
            return getKeyValues();
        }

        @Override
        public Map<String, String> getKeyLabelMap() {
            return map;
        }

        @Override
        public String getKeyLabel(String key) {
            return map.get(key);
        }

        @Override
        public void clearInternalCache() {
            //do nothing
        }
    }
}
