package org.credman0.cubegen.generator;

public class Set implements Comparable<Object>{
    protected static final String BASE_URL = "https://shop.tcgplayer.com/price-guide/magic/";

    protected String name;
    protected String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value.trim();
    }

    public String getURL() {
        return BASE_URL + value;
    }

    public Set(String name, String value) {
        this.name = name.trim();
        this.value = value.trim();
    }

    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Object o) {
        return toString().toLowerCase().compareTo(o.toString().toLowerCase());
    }
}
