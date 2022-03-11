package com.meandmyphone.chupacabraremote.providers.api;

import com.meandmyphone.chupacabraremote.properties.ObservableList;
import com.meandmyphone.chupacabraremote.properties.ObservableProperty;

/**
 * An interface via information can be obtained regarding the available and established connections.
 *
 * @param <T> Class representing an established connection
 * @param <U> Class representing an available, not-yet established connection
 */
public interface ConnectedServersProvider<T, U>  {

    ObservableProperty<T> getConnection();
    ObservableList<U> getLocatedServerDetails();

}
