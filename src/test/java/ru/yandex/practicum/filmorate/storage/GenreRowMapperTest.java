package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreRowMapperTest {

    @Test
    void mapRow_ShouldReturnCorrectGenre() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("Комедия");

        GenreRowMapper mapper = new GenreRowMapper();

        Genre genre = mapper.mapRow(rs, 1);

        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getName());
    }
}
