package com.replay.model;

import java.util.Optional;

public record Title(String id, String name, Publisher publisher) {
    public Title(String id, String name) {
        this(id, name, Publisher.UNKNOWN);
    }
}
