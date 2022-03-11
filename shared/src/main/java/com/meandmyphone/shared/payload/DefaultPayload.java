package com.meandmyphone.shared.payload;

import java.util.Objects;

public class DefaultPayload implements Payload {


    public static final Payload EMPTY = new DefaultPayload();

    private final String payload;

    private DefaultPayload() {
        this("");
    }

    public DefaultPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String getContent() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultPayload that = (DefaultPayload) o;
        return Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payload);
    }

    @Override
    public String toString() {
        return payload;
    }
}
