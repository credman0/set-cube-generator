package org.credman0;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Scraper {
    public static List<Card> scrapPage(String url, boolean sorted) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements rows = doc.getElementsByTag("tr");
        ArrayList<Card> cards = new ArrayList<>();
        for (Element row:rows) {
            if (row.getElementsByClass("productHead").size()!=0) {
                continue;
            }
            String name  = row.getElementsByClass("productDetail").get(0).getAllElements().get(0).text();
            if (name.contains("Token") || name.contains("Emblem") || isBasic(name)) {
                continue;
            }
            String priceString = row.getElementsByClass("marketPrice").get(0).getAllElements().get(0).text().trim();
            // remove dollar sign
            priceString = priceString.substring(1);
            if (priceString.equals("")) {
                return null;
            }
            cards.add(new Card(name, Double.parseDouble(priceString)));
        }
        if (sorted) {
            Collections.sort(cards);
        }
        return cards;
    }

    protected static final String SET_PAGE = "https://shop.tcgplayer.com/price-guide/magic";
    public static List<Set> scrapSets() throws IOException {
        Document doc = Jsoup.connect(SET_PAGE).get();
        ArrayList<Set> sets = new ArrayList<>();
        Element setDropDown = doc.getElementById("set");
        for (Element setDrop:setDropDown.getAllElements()) {
            String value = setDrop.val();
            if (!value.equals("")) {
                sets.add(new Set(setDrop.text(), value));
            }
        }
        return sets;
    }

    protected static boolean isBasic(String name) {
        return name.contains("Swamp") || name.contains("Island") ||name.contains("Plains") ||name.contains("Forest") ||name.contains("Mountain");
    }
}
