package com.meandmyphone.server.services.impl;

import com.meandmyphone.server.vo.Point;
import com.meandmyphone.server.vo.Screen;

public class MouseService {

    public native Point getMousePosition();

    public native void setMousePosition(int x, int y);

    public native void clickMouse(int button);

    public native void holdMouseButton(int button);

    public native void releaseMouseButton(int button);

    @Deprecated
    public native Screen[] getScreens();
}
