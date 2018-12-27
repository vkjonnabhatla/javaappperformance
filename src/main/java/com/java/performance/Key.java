package com.java.performance;

/**
 * Correct class with the presence of an equals method
 * which will not cause a memory leak.
 */
public class Key {

    Integer id;
    Key(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        Key newKey = (Key) o;
        return id.equals(newKey.id);
    }

}
