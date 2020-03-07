import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class CubeGenerator {
    public static GeneratorList generateFromSet(int quantity, double budget, Set set) throws IOException {
        GeneratorList generatorList = new GeneratorList(budget, quantity);
        List<Card> scrapedCards = Scraper.scrapPage(set.getURL(), true);
        generatorList.addRemaining(scrapedCards);
        return generatorList;
    }
}
