package org.credman0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CubeGenerator {
    protected int quantity;
    protected double budget;
    protected Set set;
    protected int numCommons;
    protected int numUncommons;

    public CubeGenerator(int quantity, double budget, Set set) {
        this.quantity = quantity;
        this.budget = budget;
        this.set = set;
        numCommons = (quantity*11)/15;
        numUncommons = (quantity*3)/15;
    }

    public GeneratorList generate(List<String> exclusions) throws IOException {
        GeneratorList generatorList = new GeneratorList(budget, quantity);
        List<Card> scrapedCards = Scraper.scrapPage(set.getURL(), true);
        List<Card> commonsList = new ArrayList<>();
        List<Card> uncommonsList = new ArrayList<>();
        List<Card> raresList = new ArrayList<>();
        for (Card card:scrapedCards) {
            boolean excluded = false;
            for (String exclusion:exclusions) {
                if (card.getName().toLowerCase().contains(exclusion.toLowerCase())) {
                    excluded = true;
                    break;
                }
            }
            if (excluded) {
                continue;
            }

            switch (card.getRarity()) {
                case 'c':
                    commonsList.add(card);
                    break;
                case 'u':
                    uncommonsList.add(card);
                    break;
                default:
                    raresList.add(card);

            }
        }
        generatorList.setExpenseWeighting(0.0);
        generatorList.addQuantity(commonsList, numCommons, 0);
        generatorList.setExpenseWeighting(0.1);
        generatorList.addQuantity(uncommonsList, numUncommons, 0);
        generatorList.setExpenseWeighting(1.0);
        generatorList.addQuantity(raresList, quantity - (numCommons + numUncommons), 3);
        return generatorList;
    }

    public int getNumCommons() {
        return numCommons;
    }

    public void setNumCommons(int numCommons) {
        this.numCommons = numCommons;
    }

    public int getNumUncommons() {
        return numUncommons;
    }

    public void setNumUncommons(int numUncommons) {
        this.numUncommons = numUncommons;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getBudget() {
        return budget;
    }

    public Set getSet() {
        return set;
    }
}
