package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public FilmService(FilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        validate(film);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.exists(film.getId())) {
            log.warn("Попытка обновить несуществующий фильм с ID {}", film.getId());
            throw new NoSuchElementException("Фильм с таким ID не найден");
        }
        validate(film);
        return filmStorage.update(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, long userId) {
        Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NoSuchElementException("Фильм не найден.");
        }

        if (!userStorage.exists(userId)) {
            throw new NoSuchElementException("Пользователь не найден.");
        }

        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, long userId) {
        Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NoSuchElementException("Фильм не найден.");
        }

        if (!userStorage.exists(userId)) {
            throw new NoSuchElementException("Пользователь не найден.");
        }

        film.getLikes().remove(userId);
    }


    public List<Film> getTopFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator
                        .comparingInt((Film f) -> f.getLikes() != null ? f.getLikes().size() : 0).reversed()
                        .thenComparingInt(Film::getId)
                )
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validate(Film film) {
        if (film.getDuration() == null || film.getDuration().isNegative() || film.getDuration().isZero()) {
            log.warn("Неверная продолжительность: {}", film.getDuration());
            throw new ValidationException("Продолжительность должна быть положительной");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.warn("Недопустимая дата релиза: {}. Дата должна быть не раньше {}", film.getReleaseDate(), CINEMA_BIRTHDAY);
            throw new ValidationException("Дата релиса не может быть раньше 28 декабря 1895 года");
        }
    }
}
