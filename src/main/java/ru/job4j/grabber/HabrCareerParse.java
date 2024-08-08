package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    public static final int NUMBER_OF_PAGES = 5;

    public static void main(String[] args) throws IOException {
        HabrCareerParse habrCareerParse = new HabrCareerParse();
        habrCareerParse.parseVacancies();
    }

    private void parseVacancies() throws IOException {
        for (int pageNumber = 1; pageNumber <= NUMBER_OF_PAGES; pageNumber++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                Element dateElementValue = dateElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String date = dateElementValue.attr("datetime");
                String description = retrieveDescription(link);
                System.out.printf("%s%nLink: %s%nCreated: %s%nDescription: %s%n%n", vacancyName, link, date, description);
            });
        }
    }

    private String retrieveDescription(String link) {
        try {
            Document document = Jsoup.connect(link).get();
            Element descriptionElement = document.select(".vacancy-description__text").first();
            return descriptionElement != null ? descriptionElement.text() : "Description not found";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
