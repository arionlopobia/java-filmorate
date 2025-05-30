package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(FilmDbStorage.class)
public class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    private Film createSampleFilm() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("Sci-fi thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(Duration.ofMinutes(148));

        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        film.setMpa(mpa);

        return film;
    }

    @Test
    void addAndGetFilm() {
        Film film = createSampleFilm();
        Film saved = filmStorage.add(film);

        Film retrieved = filmStorage.get(saved.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo("Inception");
        assertThat(retrieved.getDuration()).isEqualTo(Duration.ofMinutes(148));
        assertThat(retrieved.getMpa()).isNotNull();
        assertThat(retrieved.getMpa().getId()).isEqualTo(1);
    }

    @Test
    void updateFilm() {
        Film film = filmStorage.add(createSampleFilm());

        film.setName("Inception Updated");
        film.setDescription("Updated description");
        film.setDuration(Duration.ofMinutes(150));

        filmStorage.update(film);
        Film updated = filmStorage.get(film.getId());

        assertThat(updated.getName()).isEqualTo("Inception Updated");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getDuration()).isEqualTo(Duration.ofMinutes(150));
        assertThat(updated.getMpa().getId()).isEqualTo(1);
    }

    @Test
    void getAllFilms() {
        filmStorage.add(createSampleFilm());
        filmStorage.add(createSampleFilm());

        Collection<Film> all = filmStorage.getAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void filmExists() {
        Film film = filmStorage.add(createSampleFilm());
        boolean exists = filmStorage.exists(film.getId());

        assertThat(exists).isTrue();
        assertThat(filmStorage.exists(9999)).isFalse();
    }
}
