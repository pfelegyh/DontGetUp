package com.meandmyphone.server.services.impl;

import com.meandmyphone.server.vo.Point;

public class MouseService {

    public native Point getMousePosition();

    public native void setMousePosition(int x, int y);

    public native void clickMouse(int button);

    public native void holdMouseButton(int button);

    public native void releaseMouseButton(int button);
}
