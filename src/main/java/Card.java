public class Card implements Comparable<Object>{
    protected String name;
    protected double cost;

    public Card(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String toString() {
        return name + " = $" + cost;
    }

    @Override
    public int compareTo(Object object) {
        if (object instanceof Card) {
            return Double.compare(cost, ((Card) object).cost);
        } else if (object instanceof Number) {
            return Double.compare(cost, ((Number) object).doubleValue());
        }
        throw new IllegalArgumentException("Cannot compare Card to " + object.getClass());
    }
}
