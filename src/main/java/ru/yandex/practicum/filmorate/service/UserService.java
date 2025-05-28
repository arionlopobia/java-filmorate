package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        validate(user);
        user.setId(getNextId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано — установлено как логин: {}", user.getLogin());
        }

        userStorage.add(user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null || !userStorage.exists(user.getId())) {
            log.warn("Попытка обновить несуществующего пользователя с ID {}", user.getId());
            throw new NoSuchElementException("Пользователь с таким ID не найден");
        }

        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано при обновлении — установлено как логин: {}", user.getLogin());
        }

        userStorage.update(user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }


    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        if (user == null || friend == null) {
            throw new NoSuchElementException("Пользователь не найден.");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        if (user == null || friend == null) {
            throw new NoSuchElementException("Пользователь не найден.");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = userStorage.get(userId);
        User otherUser = userStorage.get(otherUserId);

        if (user == null || otherUser == null) {
            throw new NoSuchElementException("Пользователь не найден.");
        }

        Set<Long> commonIds = new HashSet<>(user.getFriends());
        commonIds.retainAll(otherUser.getFriends());

        return commonIds.stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    public List<User> getFriends(long userId) {
        User user = userStorage.get(userId);

        if (user == null) {
            throw new NoSuchElementException("Пользователь не найден.");
        }

        if (user.getFriends() == null) {
            return List.of();
        }

        return user.getFriends().stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }


    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: логин содержит пробелы: '{}'", user.getLogin());
            throw new ValidationException("Логин не должен содержать пробелов");
        }
    }

    private long getNextId() {
        return userStorage.getAll().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0) + 1;
    }

}
