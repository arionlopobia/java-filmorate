package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.validation.ValidationException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Попытка обновить несуществующий фильм с ID {}", film.getId());
            throw new ValidationException("Фильм с таким ID не найден");
        }
        validate(film);
        films.put(film.getId(), film);
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private void validate(Film film) {
        if (film.getDuration() == null || film.getDuration().isNegative() || film.getDuration().isZero()) {
            log.warn("Неверная продолжительность: {}", film.getDuration());
            throw new ValidationException("Продолжительность должна быть положительной");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Недопустимая дата релиза: {}. Дата должна быть не раньше {}", film.getReleaseDate(), CINEMA_BIRTHDAY);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private int getNextId() {
        return films.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
    }
}