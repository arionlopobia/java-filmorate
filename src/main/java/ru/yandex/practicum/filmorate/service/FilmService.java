package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikeDbStorage likeStorage;
    private final GenreStorage genreStorage;
    private final MpaRatingStorage mpaStorage;

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            FilmLikeDbStorage likeStorage,
            GenreStorage genreStorage,
            MpaRatingStorage mpaStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film addFilm(Film film) {
        validateCore(film);

        if (film.getMpa() == null) {
            throw new ValidationException("MPA-рейтинг не может быть null");
        }
        mpaStorage.getById(film.getMpa().getId());

        List<Genre> genres = film.getGenres() == null
                ? new ArrayList<>()
                : film.getGenres().stream()
                .map(g -> genreStorage.getById(g.getId()))
                .collect(Collectors.toList());
        film.setGenres(genres);

        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.exists(film.getId())) {
            throw new NoSuchElementException("Фильм с таким ID не найден");
        }
        validateCore(film);

        if (film.getMpa() == null) {
            throw new ValidationException("MPA-рейтинг не может быть null");
        }
        mpaStorage.getById(film.getMpa().getId());

        List<Genre> genres = film.getGenres() == null
                ? new ArrayList<>()
                : film.getGenres().stream()
                .map(g -> genreStorage.getById(g.getId()))
                .collect(Collectors.toList());
        film.setGenres(genres);

        return filmStorage.update(film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAll().stream()
                .peek(film -> {
                    film.setMpa(mpaStorage.getById(film.getMpa().getId()));
                    List<Genre> uniqueGenres = genreStorage.getByFilmId(film.getId()).stream()
                            .distinct()
                            .collect(Collectors.toList());
                    film.setGenres(uniqueGenres);
                })
                .collect(Collectors.toList());
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.get(id);
        if (film == null) {
            throw new NoSuchElementException("Фильм с таким ID не найден");
        }
        film.setMpa(mpaStorage.getById(film.getMpa().getId()));
        List<Genre> uniqueGenres = genreStorage.getByFilmId(id).stream()
                .distinct()
                .collect(Collectors.toList());
        film.setGenres(uniqueGenres);
        return film;
    }

    public void addLike(int filmId, long userId) {
        getFilmById(filmId);
        if (!userStorage.exists(userId)) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, long userId) {
        getFilmById(filmId);
        if (!userStorage.exists(userId)) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        likeStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) ->
                        Integer.compare(
                                likeStorage.getLikes(f2.getId()).size(),
                                likeStorage.getLikes(f1.getId()).size()
                        ))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateCore(Film film) {
        if (film.getDuration() == null || film.getDuration().isNegative() || film.getDuration().isZero()) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }
}
