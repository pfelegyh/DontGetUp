package com.meandmyphone.server.vo;

import java.util.Objects;

public class Screen {

    private int index;
    private int left;
    private int top;
    private int right;
    private int bottom;

    public int getIndex() {
        return index;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Screen screen = (Screen) o;
        return index == screen.index &&
                left == screen.left &&
                top == screen.top &&
                right == screen.right &&
                bottom == screen.bottom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, left, top, right, bottom);
    }

    @Override
    public String toString() {
        return "Screen{" +
                "index=" + index +
                ", left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }
}
