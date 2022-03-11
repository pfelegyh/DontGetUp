package com.meandmyphone.chupacabraremote.properties;


import java.util.Observable;

/**
 * An observable property
 * @param <T> generic type of the property to be observed
 */
public class ObservableProperty<T> extends Observable {

    private T observed;

    public ObservableProperty(T observed) {
        this.observed = observed;
    }

    public T getValue() {
        return observed;
    }

    public void setValue(T observed) {
        this.observed = observed;
        setChanged();
        notifyObservers();
    }
}