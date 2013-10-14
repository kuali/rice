/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * List implementation for internal use by a lifecycle element.
 *
 * <p>Mutability of the list will follow the semantics for the lifecycle element.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LifecycleAwareList<E> implements List<E>, UifCloneable, Serializable {
    private static final long serialVersionUID = -8971217230511446882L;

    /**
     * Delegating list iterator proxy.
     *
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private class ListIter implements ListIterator<E> {

        private final ListIterator<E> delegate;

        /**
         * @see LifecycleAwareList#listIterator()
         */
        private ListIter() {
            this.delegate = LifecycleAwareList.this.delegate.listIterator();
        }

        /**
         * @see LifecycleAwareList#listIterator(int)
         */
        private ListIter(int index) {
            this.delegate = LifecycleAwareList.this.delegate.listIterator(index);
        }

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public E next() {
            return this.delegate.next();
        }

        @Override
        public boolean hasPrevious() {
            return this.delegate.hasPrevious();
        }

        @Override
        public E previous() {
            return this.delegate.previous();
        }

        @Override
        public int nextIndex() {
            return this.delegate.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.delegate.previousIndex();
        }

        @Override
        public void remove() {
            lifecycleElement.checkMutable(true);
            this.delegate.remove();
        }

        @Override
        public void set(E e) {
            lifecycleElement.checkMutable(true);
            this.delegate.set(e);
        }

        @Override
        public void add(E e) {
            lifecycleElement.checkMutable(true);
            this.delegate.add(e);
        }

    }

    /**
     * Delegating iterator proxy.
     *
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private class Iter implements Iterator<E> {

        private final Iterator<E> delegate;

        /**
         * @see LifecycleAwareList#iterator()
         */
        private Iter() {
            this.delegate = LifecycleAwareList.this.delegate.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override
        public E next() {
            return this.delegate.next();
        }

        @Override
        public void remove() {
            lifecycleElement.checkMutable(true);
            this.delegate.remove();
        }
    }

    /**
     * The component this list is related to.
     */
    private final LifecycleElement lifecycleElement;

    /**
     * Delegating list implementation.
     */
    private List<E> delegate;

    /**
     * Create a new list instance.
     *
     * @param lifecycleElement The lifecycle element to use for mutability checks.
     */
    public LifecycleAwareList(LifecycleElement lifecycleElement) {
        this.lifecycleElement = lifecycleElement;
        this.delegate = Collections.emptyList();
    }

    /**
     * Create a new list instance, based on another list.
     *
     * @param lifecycleElement The lifecycle element to use for mutability checks.
     * @param delegate The list to wrap.
     */
    public LifecycleAwareList(LifecycleElement lifecycleElement, List<E> delegate) {
        this.lifecycleElement = lifecycleElement;
        this.delegate = delegate;
    }

    /**
     * Ensure that the delegate list can be modified.
     */
    private void ensureMutable() {
        lifecycleElement.checkMutable(true);

        if (delegate == Collections.EMPTY_LIST) {
            delegate = new ArrayList<E>();
        }
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iter();
    }

    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.delegate.toArray(a);
    }

    @Override
    public boolean add(E e) {
        ensureMutable();
        return this.delegate.add(e);
    }

    @Override
    public boolean remove(Object o) {
        lifecycleElement.checkMutable(true);
        return delegate != Collections.EMPTY_LIST && delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        ensureMutable();
        return this.delegate.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        ensureMutable();
        return this.delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        lifecycleElement.checkMutable(true);
        return delegate != Collections.EMPTY_LIST && this.delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        lifecycleElement.checkMutable(true);
        return delegate != Collections.EMPTY_LIST && this.delegate.retainAll(c);
    }

    @Override
    public void clear() {
        if (this.delegate != Collections.EMPTY_LIST) {
            this.delegate.clear();
        }
    }

    @Override
    public boolean equals(Object o) {
        return this.delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public E get(int index) {
        return this.delegate.get(index);
    }

    @Override
    public E set(int index, E element) {
        lifecycleElement.checkMutable(true);
        return this.delegate.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        ensureMutable();
        this.delegate.add(index, element);
    }

    @Override
    public E remove(int index) {
        lifecycleElement.checkMutable(true);
        return this.delegate.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        ensureMutable();
        return new ListIter();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        ensureMutable();
        return new ListIter(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new LifecycleAwareList<E>(lifecycleElement, this.delegate.subList(fromIndex, toIndex));
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
