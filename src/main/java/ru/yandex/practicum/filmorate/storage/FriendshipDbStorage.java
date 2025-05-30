package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FriendshipDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(long userId, long friendId, FriendshipStatus status) {
        String sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, status.name());
    }

    public void removeFriend(long userId, long friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public Map<Long, FriendshipStatus> getFriends(long userId) {
        String sql = "SELECT friend_id, status FROM friendships WHERE user_id = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);

        Map<Long, FriendshipStatus> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Long fid = ((Number) row.get("friend_id")).longValue();
            FriendshipStatus st = FriendshipStatus.valueOf((String) row.get("status"));
            result.put(fid, st);
        }
        return result;
    }
}
