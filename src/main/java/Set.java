public class Set {
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
}
