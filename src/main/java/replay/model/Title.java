package replay.model;

import java.util.Optional;

public record Title(String id, String name, Optional<Publisher> publisher) {
    public Title(String id, String name) {
        this(id, name, Optional.empty());
    }
}
