package com.meandmyphone.chupacabraremote.properties;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

/**
 * A list that triggers an event when the contents change, does not trigger an change event
 * automatically, when a contained element' state changes (e.g. an element property has changed)
 *
 * @param <E> generic type of elements present in the list
 */
public class ObservableList<E> extends Observable implements List<E> {

    private final List<E> observed = new LinkedList<>();

    @Override
    public synchronized void setChanged() {
        super.setChanged();
    }

    @Override
    public int size() {
        return observed.size();
    }

    @Override
    public boolean isEmpty() {
        return observed.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return observed.contains(o);
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return observed.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return observed.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] ts) {
        return observed.<T>toArray(ts);
    }

    @Override
    public boolean add(E e) {
        boolean retVal = observed.add(e);
        setChanged();
        notifyObservers();
        return retVal;
    }

    @Override
    public boolean remove(Object o) {
        boolean retVal = observed.remove(o);
        setChanged();
        notifyObservers();
        return retVal;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return observed.containsAll(collection);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> collection) {
        boolean retVal = observed.addAll(collection);
        setChanged();
        notifyObservers();
        return retVal;
    }

    @Override
    public boolean addAll(int i, @NonNull Collection<? extends E> collection) {
        boolean b = observed.addAll(i, collection);
        setChanged();
        notifyObservers();
        return b;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        boolean b = observed.removeAll(collection);
        setChanged();
        notifyObservers();
        return b;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        boolean b = observed.retainAll(collection);
        setChanged();
        notifyObservers();
        return b;
    }

    @Override
    public void clear() {
        observed.clear();
        setChanged();
        notifyObservers();
    }

    @Override
    public E get(int i) {
        return observed.get(i);
    }

    @Override
    public E set(int i, E e) {
        E e1 = observed.set(i, e);
        setChanged();
        notifyObservers();
        return e1;
    }

    @Override
    public void add(int i, E e) {
        observed.add(i, e);
        setChanged();
        notifyObservers();
    }

    @Override
    public E remove(int i) {
        E e = observed.remove(i);
        setChanged();
        notifyObservers();
        return e;
    }

    @Override
    public int indexOf(Object o) {
        return observed.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return observed.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator() {
        return observed.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int i) {
        return observed.listIterator(i);
    }

    @NonNull
    @Override
    public List<E> subList(int i, int i1) {
        return observed.subList(i, i1);
    }
}
