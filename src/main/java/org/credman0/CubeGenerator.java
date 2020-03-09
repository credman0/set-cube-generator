package org.credman0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CubeGenerator {
    protected int quantity;
    protected double budget;
    protected Set set;
    protected int packSize = 15;
    protected int commonsPer = 11;
    protected int uncommonsPer = 3;
    protected int numCommons;
    protected int numUncommons;
    protected int rerollCommons = 0;
    protected int rerollUncommons = 0;
    protected int rerollRares = 3;


    public CubeGenerator(int quantity, double budget, Set set) {
        this.quantity = quantity;
        this.budget = budget;
        this.set = set;
        numCommons = (quantity*commonsPer)/packSize;
        numUncommons = (quantity*uncommonsPer)/packSize;
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
        generatorList.addQuantity(commonsList, numCommons, rerollCommons);
        generatorList.setExpenseWeighting(0.0);
        generatorList.addQuantity(uncommonsList, numUncommons, rerollUncommons);
        generatorList.setExpenseWeighting(1.0);
        generatorList.addQuantity(raresList, quantity - (numCommons + numUncommons), rerollRares);
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

    public int getRerollCommons() {
        return rerollCommons;
    }

    public void setRerollCommons(int rerollCommons) {
        this.rerollCommons = rerollCommons;
    }

    public int getRerollUncommons() {
        return rerollUncommons;
    }

    public void setRerollUncommons(int rerollUncommons) {
        this.rerollUncommons = rerollUncommons;
    }

    public int getRerollRares() {
        return rerollRares;
    }

    public void setRerollRares(int rerollRares) {
        this.rerollRares = rerollRares;
    }

    public int getPackSize() {
        return packSize;
    }

    public void setPackSize(int packSize) {
        this.packSize = packSize;
    }

    public int getCommonsPer() {
        return commonsPer;
    }

    public void setCommonsPer(int commonsPer) {
        this.commonsPer = commonsPer;
        numCommons = (quantity*commonsPer)/packSize;
    }

    public int getUncommonsPer() {
        return uncommonsPer;
    }

    public void setUncommonsPer(int uncommonsPer) {
        this.uncommonsPer = uncommonsPer;
        numUncommons = (quantity*uncommonsPer)/packSize;
    }
}
