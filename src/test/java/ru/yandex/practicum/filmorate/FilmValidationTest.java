package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ofMinutes(90));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ofMinutes(90));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldFailWhenReleaseDateIsNull() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setDuration(Duration.ofMinutes(90));
        film.setReleaseDate(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldPassWhenAllFieldsAreValid() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Короткое описание");
        film.setDuration(Duration.ofMinutes(120));
        film.setReleaseDate(LocalDate.of(2020, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }
}
