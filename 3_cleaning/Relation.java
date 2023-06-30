package stage3;

import java.util.List;

public class Relation {
    final String name;
    final List<String> attributes;

    public Relation(String name, List<String> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public List<String> getAttributes() {
        return attributes;
    }
}
