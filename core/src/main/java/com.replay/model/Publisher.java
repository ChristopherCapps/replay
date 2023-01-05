package com.replay.model;

public record Publisher(String id, String name) {
    public static Publisher UNKNOWN = new Publisher("UNKNOWN", "Unknown");
}
