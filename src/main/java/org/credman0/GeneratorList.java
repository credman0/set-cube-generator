package org.credman0;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneratorList {
    protected double budget;
    protected int quantityTotal;
    Random rand = new Random();
    protected ArrayList<Card> listInternal = new ArrayList<>();
    protected double budgetUsed = 0.0;
    protected double expenseWeighting = 0.8;

    public GeneratorList(double budget, int quantity) {
        this.budget = budget;
        this.quantityTotal = quantity;
    }

    public List<Card> getList() {
        return listInternal;
    }

    protected double getAverageRemainingCost() {
        return (budget- getBudgetUsed()) / (quantityTotal - listInternal.size());
    }

    public void addQuantity(List<Card> setList, int quantity, int avoidDuplicates) {
        for (int i = 0; i < quantity; i++) {
            addFromList(setList, avoidDuplicates);
        }
    }

    /**
     *
     * @param setList must be sorted
     */
    public void addFromList (List<Card> setList, int avoidDuplicates) {
        double avgRemaining = getAverageRemainingCost();
        int selectionIndexStart = setList.size()/2;
        int jumpSize = selectionIndexStart/2;
        // start at median
        double selectionCost = setList.get(selectionIndexStart).getCost();
        if (rand.nextDouble() < expenseWeighting) {
            if (avgRemaining>=selectionCost) {
//                while (jumpSize > 0 && avgRemaining > selectionCost/2) {
//                    selectionIndexStart += jumpSize;
//                    jumpSize /= 2;
//                    selectionCost = setList.get(selectionIndexStart).getCost();
//                }
                int maxIndex = Collections.binarySearch(setList,  budget-budgetUsed);
                maxIndex = maxIndex<0? -maxIndex - 1:maxIndex;
                int selectedIndex = rand.nextInt(maxIndex - selectionIndexStart) + selectionIndexStart;
                Card selectedCard = setList.get(selectedIndex);
                // don't blow the whole budget at once
                while (selectedCard.getCost()>budget/2) {
                    selectedIndex = rand.nextInt(setList.size() - selectionIndexStart) + selectionIndexStart;
                    selectedCard = setList.get(selectedIndex);
                }
                addCard(selectedCard);
            } else {
                // median is too expensive
//                while (jumpSize > 0 && avgRemaining < selectionCost*2) {
//                    selectionIndexStart -= jumpSize;
//                    jumpSize /= 2;
//                    selectionCost = setList.get(selectionIndexStart).getCost();
//                }
                int selectedIndex = rand.nextInt(selectionIndexStart);
                Card selectedCard = setList.get(selectedIndex);
                if (avoidDuplicates>0) {
                    if (listInternal.contains(selectedCard)) {
                        addFromList(setList, avoidDuplicates-1);
                    } else {
                        addCard(selectedCard);
                    }
                } else {
                    addCard(selectedCard);
                }
            }
        } else {
            int allowedCostMultiplier = quantityTotal-listInternal.size() > 2?2:1;
            int maxIndex = Collections.binarySearch(setList,  getAverageRemainingCost()*allowedCostMultiplier);
            maxIndex = maxIndex<0? -maxIndex - 1:maxIndex;
            Card selectedCard = setList.get(rand.nextInt(maxIndex));
            addCard(selectedCard);
        }
    }

    protected void addCard(Card card) {
        budgetUsed = getBudgetUsed() + card.getCost();
        listInternal.add(card);
    }

    public double getBudgetUsed() {
        return budgetUsed;
    }

    public double getExpenseWeighting() {
        return expenseWeighting;
    }

    public void setExpenseWeighting(double expenseWeighting) {
        this.expenseWeighting = expenseWeighting;
    }

    public String toString() {
        return getList().toString();
    }
}
