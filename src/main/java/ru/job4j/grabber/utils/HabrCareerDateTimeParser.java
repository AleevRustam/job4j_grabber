package ru.job4j.grabber.utils;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        if (parse == null || parse.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        try {
            return LocalDateTime.parse(parse, formatter);
        } catch (DateTimeException e) {
            e.printStackTrace();
            return null;
        }

    }
}