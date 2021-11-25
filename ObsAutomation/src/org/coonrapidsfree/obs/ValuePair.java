package org.coonrapidsfree.obs;

public class ValuePair {

    String name;
    int value;

    public ValuePair(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String toString() {
        return name + ":" + value;
    }
}
