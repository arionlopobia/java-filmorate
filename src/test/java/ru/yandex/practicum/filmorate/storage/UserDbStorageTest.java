package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
public class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    public void testAddAndFindUserById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User added = userStorage.add(user);
        Optional<User> retrieved = Optional.ofNullable(userStorage.get(added.getId()));

        assertThat(retrieved).isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u.getEmail()).isEqualTo("test@example.com");
                    assertThat(u.getLogin()).isEqualTo("testuser");
                });
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("old@example.com");
        user.setLogin("oldlogin");
        user.setName("Old Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User added = userStorage.add(user);

        added.setEmail("new@example.com");
        added.setLogin("newlogin");
        added.setName("New Name");

        userStorage.update(added);

        User updated = userStorage.get(added.getId());

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getLogin()).isEqualTo("newlogin");
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("a@mail.com");
        user1.setLogin("a");
        user1.setName("A");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("b@mail.com");
        user2.setLogin("b");
        user2.setName("B");
        user2.setBirthday(LocalDate.of(1991, 1, 1));

        userStorage.add(user1);
        userStorage.add(user2);

        assertThat(userStorage.getAll()).hasSize(2);
    }

    @Test
    public void testUserExists() {
        User user = new User();
        user.setEmail("exists@example.com");
        user.setLogin("existslogin");
        user.setName("Exists");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User saved = userStorage.add(user);

        assertThat(userStorage.exists(saved.getId())).isTrue();
        assertThat(userStorage.exists(9999L)).isFalse();
    }
}
