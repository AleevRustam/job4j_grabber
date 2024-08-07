package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HabrCareerDateTimeParserTest {

    @Test
    void whenParseValidDateTimeThenLocalDateTime() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String date = "2024-08-03T10:27:15+03:00";
        LocalDateTime expected = LocalDateTime.of(
                2024, 8, 3, 10, 27, 15);
        LocalDateTime actual = parser.parse(date);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenParseInvalidDateTimeThenReturnNull() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String date = "invalid_date";
        assertThat(parser.parse(date)).isNull();
    }

    @Test
    void whenParseEmptyDateTimeThenReturnNull() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        assertThat(parser.parse("")).isNull();
    }

    @Test
    void whenParseNullDateTimeThenReturnNull() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        assertThat(parser.parse(null)).isNull();
    }

}