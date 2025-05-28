package net.limit.cubliminal.util;

public record BooleanPair(boolean first, boolean second) {

    public static BooleanPair of(boolean first, boolean second) {
        return new BooleanPair(first, second);
    }
}
