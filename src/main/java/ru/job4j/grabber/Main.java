package ru.job4j.grabber;

import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String sourceLink = "https://career.habr.com";
        HabrCareerDateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(dateTimeParser);
        List<Post> posts = habrCareerParse.list(sourceLink);
        posts.forEach(System.out::println);

    }
}
