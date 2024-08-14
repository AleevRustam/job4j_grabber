package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    public static final int NUMBER_OF_PAGES = 3;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
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

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        for (int pageNumber = 1; pageNumber <= NUMBER_OF_PAGES; pageNumber++) {
            String fullLink = "%s%s%d%s".formatted(link, PREFIX, pageNumber, SUFFIX);
            try {
                Connection connection = Jsoup.connect(fullLink);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    Element titleElement = row.select(".vacancy-card__title").first();
                    Element linkElement = titleElement.child(0);
                    Element dateElement = row.select(".vacancy-card__date").first();
                    Element dateElementValue = dateElement.child(0);
                    String vacancyName = titleElement.text();
                    String vacancyLink = String.format("%s%s", link, linkElement.attr("href"));
                    String date = dateElementValue.attr("datetime");
                    String description = retrieveDescription(vacancyLink);
                    posts.add(new Post(
                            0,
                            vacancyName,
                            vacancyLink,
                            dateTimeParser.parse(date),
                            description
                    ));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return posts;
    }
}

