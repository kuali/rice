/*
 * Copyright 2008-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class acts like an LRU cache, automatically purging contents when it gets above a certain size. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
//TODO: consider using google collections
public class MaxSizeMap<K,V> extends LinkedHashMap<K,V> {

        private int maxSize;
        
        /**
         * @param maxSize
         */
        public MaxSizeMap( int maxSize  ) {
                this( maxSize, false );
        }
        /**
         * @param maxSize
         * @param accessOrder Whether to sort in the order accessed rather than the order inserted.
         */
        public MaxSizeMap( int maxSize, boolean accessOrder ) {
                super( maxSize / 2, 0.75f, accessOrder );
                this.maxSize = maxSize;
        }
        
        /**
         * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
         */
        @Override
        protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
                return size() > maxSize;
        }
        
}