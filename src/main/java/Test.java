import java.io.IOException;

public class Test {
    public static void main (String[] args) {
        try {
            System.out.println(Scraper.scrapPage("https://shop.tcgplayer.com/price-guide/magic/theros-beyond-death", true));
            Scraper.scrapSets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
