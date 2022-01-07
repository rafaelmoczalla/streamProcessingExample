package com.fonda.b6.examples.data;

public class Feedback {
    Long id;
    Long group;
    String message;

    public Feedback(Long id, Long group, String message) {
        this.id = id;
        this.group = group;
        this.message = message;
    }

    public Long getId() {
        return this.id;
    }

    public Long getGroup() {
        return this.group;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", group=" + group +
                ", message=" + message +
                "}";
    }
}