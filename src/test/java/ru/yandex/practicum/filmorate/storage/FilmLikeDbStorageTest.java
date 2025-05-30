package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(FilmLikeDbStorage.class)
public class FilmLikeDbStorageTest {

    @Autowired
    private FilmLikeDbStorage storage;

    @Autowired
    private JdbcTemplate jdbc;

    private final long filmId = 1L;
    private final long userId = 1L;

    @BeforeEach
    void setUp() {
        jdbc.update("INSERT INTO users (id, email, login, name, birthday) VALUES (1, 'u@u.ru', 'u','U','2000-01-01')");
        jdbc.update("INSERT INTO films (id, name, description, release_date, duration, mpa_id) VALUES " +
                "(1, 'F','D','2000-01-01', 100, 1)");
    }


    @Test
    void addLikeAndGet() {
        storage.addLike(filmId, userId);
        Set<Long> likes = storage.getLikes(filmId);

        assertThat(likes).hasSize(1).contains(userId);
    }

    @Test
    void removeLike() {
        storage.addLike(filmId, userId);
        storage.removeLike(filmId, userId);

        Set<Long> likes = storage.getLikes(filmId);
        assertThat(likes).isEmpty();
    }
}
