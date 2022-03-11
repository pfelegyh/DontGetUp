package com.meandmyphone.chupacabraremote.service.api;

import com.meandmyphone.chupacabraremote.service.impl.Screen;
import com.meandmyphone.chupacabraremote.service.impl.ScreenConnection;

import java.util.List;

/**
 * A high level service responsible for various mouse related operations, and it also provides
 * information about the available screens on the connected computer, and builds a "fast" connection
 * used for "real-time" mouse movement
 */
public interface MouseService {

    ScreenConnection buildMouseConnection();
    void clickCurrentMousePosition(int button);
    void holdMouseButton(int button);
    void releaseMouseButton(int button);

    @Deprecated
    void clickScreen(int index, int button, int x, int y);
    @Deprecated
    List<Screen> getScreens();
    @Deprecated
    void moveMouseTo(int x, int y);
    @Deprecated
    void moveMouseBy(double vectorX, double vectorY);
}
