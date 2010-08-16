/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.lookup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;


public class CollectionIncomplete<T> implements List<T>, RandomAccess, Serializable {

    private static final long serialVersionUID = 8683452581122892189L;
	private final List<T> list;
    private Long actualSizeIfTruncated;


    /**
     * @param collection
     * @param actualSizeIfTruncated
     */
    public CollectionIncomplete(Collection<T> collection, Long actualSizeIfTruncated) {
        super();
        this.list = new ArrayList<T>(collection);
        this.actualSizeIfTruncated = actualSizeIfTruncated;
    }

    /**
     * @param arg0
     * @param arg1
     */
    public void add(int arg0, T arg1) {
        list.add(arg0, arg1);
    }

    /**
     * @param arg0
     * @return
     */
    public boolean add(T arg0) {
        return list.add(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     */
    public boolean addAll(int arg0, Collection<? extends T> arg1) {
        return list.addAll(arg0, arg1);
    }

    /**
     * @param arg0
     * @return
     */
    public boolean addAll(Collection<? extends T> arg0) {
        return list.addAll(arg0);
    }

    /**
     * 
     */
    public void clear() {
        list.clear();
    }

    /**
     * @param arg0
     * @return
     */
    public boolean contains(Object arg0) {
        return list.contains(arg0);
    }

    /**
     * @param arg0
     * @return
     */
    public boolean containsAll(Collection<?> arg0) {
        return list.containsAll(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object arg0) {
        return list.equals(arg0);
    }

    /**
     * @param arg0
     * @return
     */
    public T get(int arg0) {
        return list.get(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return list.hashCode();
    }

    /**
     * @param arg0
     * @return
     */
    public int indexOf(Object arg0) {
        return list.indexOf(arg0);
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * @return
     */
    public Iterator<T> iterator() {
        return list.iterator();
    }

    /**
     * @param arg0
     * @return
     */
    public int lastIndexOf(Object arg0) {
        return list.lastIndexOf(arg0);
    }

    /**
     * @return
     */
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    /**
     * @param arg0
     * @return
     */
    public ListIterator listIterator(int arg0) {
        return list.listIterator(arg0);
    }

    /**
     * @param arg0
     * @return
     */
    public T remove(int arg0) {
        return list.remove(arg0);
    }

    /**
     * @param arg0
     * @return
     */
    public boolean remove(Object arg0) {
        return list.remove(arg0);
    }

    /**
     * @param arg0
     * @return
     */
    public boolean removeAll(Collection<?> arg0) {
        return list.removeAll(arg0);
    }

    /**
     * @param arg0
     * @return
     */
    public boolean retainAll(Collection<?> arg0) {
        return list.retainAll(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     */
    public T set(int arg0, T arg1) {
        return list.set(arg0, arg1);
    }

    /**
     * @return
     */
    public int size() {
        return list.size();
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     */
    public List<T> subList(int arg0, int arg1) {
        return list.subList(arg0, arg1);
    }

    /**
     * @return
     */
    public Object[] toArray() {
        return list.toArray();
    }

    /**
     * @param arg0
     * @return
     */
    public <T> T[] toArray(T[] arg0) {
        return list.toArray(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return list.toString();
    }

    /**
     * @return Returns the actualSizeIfTruncated.
     */
    public Long getActualSizeIfTruncated() {
        return actualSizeIfTruncated;
    }

    /**
     * @param actualSize The actualSizeIfTruncated to set.
     */
    public void setActualSizeIfTruncated(Long actualSizeIfTruncated) {
        this.actualSizeIfTruncated = actualSizeIfTruncated;
    }
}
