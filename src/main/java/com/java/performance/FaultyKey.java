package com.java.performance;

/**
 * Faulty class lacking the presence of an equals method
 * causing a possible memory leak.
 */
public class FaultyKey {
    Integer id;

    public FaultyKey(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
