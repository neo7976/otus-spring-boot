package ru.dsobin.otus.spring.boot.quiz.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.*;

class CsvQuestionDaoLinesTest {

    @Test
    @DisplayName("CsvQuestionDao.readLines читает 5 строк из questions-test.csv в test-resources")
    void shouldReadAllLinesFromTestCsv() {
        CsvQuestionDao dao = new CsvQuestionDao(new ClassPathResource("questions-test.csv"));
        var lines = dao.readLines();

        assertNotNull(lines);
        assertEquals(5, lines.size());
        assertEquals("What is Java?,0,Programming language,Scripting language,Markup language", lines.get(0));
        assertEquals("Free answer: What is your name?,(free)", lines.get(4));
    }
}
