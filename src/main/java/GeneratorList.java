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

    public void addRemaining(List<Card> setList) {
        for (int i = listInternal.size(); i < quantityTotal; i++) {
            addFromList(setList);
        }
    }

    /**
     *
     * @param setList must be sorted
     */
    public void addFromList (List<Card> setList) {
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
                int selectedIndex = rand.nextInt(setList.size() - selectionIndexStart) + selectionIndexStart;
                Card selectedCard = setList.get(selectedIndex);
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
                setBudgetUsed(getBudgetUsed() + selectedCard.getCost());
                listInternal.add(selectedCard);
            }
        } else {
            int allowedCostMultiplier = quantityTotal-listInternal.size() > 2?2:1;
            int maxIndex = Collections.binarySearch(setList,  getAverageRemainingCost()*allowedCostMultiplier);
            Card selectedCard = setList.get(rand.nextInt(Math.abs(maxIndex)));
            addCard(selectedCard);
        }
    }

    protected void addCard(Card card) {
        setBudgetUsed(getBudgetUsed() + card.getCost());
        listInternal.add(card);
    }

    public double getBudgetUsed() {
        return budgetUsed;
    }

    public void setBudgetUsed(double budgetUsed) {
        this.budgetUsed = budgetUsed;
    }
}
