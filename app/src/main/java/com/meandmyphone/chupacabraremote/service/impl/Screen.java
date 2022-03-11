package com.meandmyphone.chupacabraremote.service.impl;

import com.meandmyphone.chupacabraremote.R;

import java.util.Objects;

/**
 * An object representation of a surface of a monitor connected to the server.
 */
public class Screen {

    private final int index;
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    public Screen(int index, int left, int top, int right, int bottom) {
        this.index = index;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public static int getNumberImageResource(int index) {
        switch (index % 6) {
            case 0 : return R.drawable.ic_looks_one_black_24dp;
            case 1 : return R.drawable.ic_looks_two_black_24dp;
            case 2 : return R.drawable.ic_looks_3_black_24dp;
            case 3 : return R.drawable.ic_looks_4_black_24dp;
            case 4 : return R.drawable.ic_looks_5_black_24dp;
            default: return R.drawable.ic_looks_6_black_24dp;
        }
    }

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
