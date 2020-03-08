package org.credman0;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Card implements Comparable<Object>{
    protected String name;
    protected double cost;
    protected char rarity;
    protected static final NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();

    public Card(String name, double cost, char rarity) {
        this.name = name;
        this.cost = cost;
        this.rarity = rarity;
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

    public char getRarity() {
        return rarity;
    }

    public void setRarity(char rarity) {
        this.rarity = rarity;
    }

    public String toString() {
        return name + " (" + moneyFormat.format(cost)+")";
    }

    @Override
    public int compareTo(Object object) {
        if (object instanceof Card) {
            return Double.compare(cost, ((Card) object).cost);
        } else if (object instanceof Number) {
            return Double.compare(cost, ((Number) object).doubleValue());
        }
        throw new IllegalArgumentException("Cannot compare org.credman0.Card to " + object.getClass());
    }

    public boolean equals(Object o) {
        if (!(o instanceof Card)) {
            return false;
        }
        Card other = (Card) o;
        return other.getName().equals(getName());
    }
}
