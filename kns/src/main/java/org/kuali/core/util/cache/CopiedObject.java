/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.util.cache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * Container class to simplify getting both a deepCopied object and its size returned from a single call to deepCopy.
 */
public class CopiedObject<T extends Serializable> {
    //private Serializable content;
    private byte[] content;
    private int size;
    private int oldSize;

    public CopiedObject() {
        oldSize = -1;
    }

    public CopiedObject( T cacheableObject ) {
        oldSize = -1;
        setContent( cacheableObject );
    }

    /**
     * @return current value of bytes.
     */
    public int getSize() {
        return size;
    }

    /**
     * @return current value of cacheableObject.
     */
    public T getContent() {
        T copy = null;
        if (content != null) {
            ObjectInputStream ois = null;
            try {
                FastByteArrayInputStream deserializer = new FastByteArrayInputStream(content,size);
                ois = new ObjectInputStream(deserializer);
                copy = (T) ois.readObject();
            }
            catch (IOException e) {
                throw new CacheException("unable to complete getContent()", e);
            }
            catch (ClassNotFoundException e) {
                throw new CacheException("unable to complete getContent()", e);
            }
            finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                }
                catch (IOException e) {
                    // ignoring this IOException, since the streams are going to be abandoned now anyway
                }
            }
        }
        return copy;
    }

    /**
     * Sets the cacheableObject attribute value.
     * 
     * @param cacheableObject The cacheableObject to set.
     */
    public void setContent(T cacheableObject) {
        int copySize = 0;
        if (cacheableObject != null) {
            ObjectOutputStream oos = null;
            try {
                FastByteArrayOutputStream serializer = new FastByteArrayOutputStream();
                oos = new ObjectOutputStream(serializer);
                oos.writeObject(cacheableObject);

                if ( content != null ) {
                    oldSize = size;
                }
                size = serializer.getSize();
                content = serializer.getByteArray();
            } catch (IOException e) {
                throw new CacheException("unable to complete deepCopy from src '" + cacheableObject.toString() + "'", e);
            }
            finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                } catch (IOException e) {
                    // ignoring this IOException, since the streams are going to be abandoned now anyway
                }
            }
        }
    }


    /**
     * @return current value of oldSize.
     */
    public int getOldSize() {
        return oldSize;
    }
}
