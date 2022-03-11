package com.meandmyphone.chupacabraremote.service.api;

import com.meandmyphone.chupacabraremote.service.impl.MouseConnection;

/**
 * A high level service responsible for various mouse related operations, and it also provides
 * information about the available screens on the connected computer, and builds a "fast" connection
 * used for "real-time" mouse movement
 */
public interface MouseService {

    MouseConnection buildMouseConnection();
    void clickCurrentMousePosition(int button);
    void holdMouseButton(int button);
    void releaseMouseButton(int button);
}
