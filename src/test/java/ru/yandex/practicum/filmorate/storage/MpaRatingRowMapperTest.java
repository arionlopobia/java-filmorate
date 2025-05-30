package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.mapper.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MpaRatingRowMapperTest {

    @Test
    void mapRow_ShouldReturnCorrectMpaRating() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenReturn(3);
        when(rs.getString("name")).thenReturn("PG-13");

        MpaRatingRowMapper mapper = new MpaRatingRowMapper();

        MpaRating mpa = mapper.mapRow(rs, 1);

        assertEquals(3, mpa.getId());
        assertEquals("PG-13", mpa.getName());
    }
}
