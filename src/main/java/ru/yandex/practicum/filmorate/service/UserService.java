package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipStorage;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage,
            FriendshipDbStorage friendshipStorage
    ) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public User addUser(User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано — установлено как логин: {}", user.getLogin());
        }
        return userStorage.add(user);
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
        return userStorage.update(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getUserById(long id) {
        User user = userStorage.get(id);
        if (user == null) {
            throw new NoSuchElementException("Пользователь с таким ID не найден");
        }
        return user;
    }

    public void addFriend(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);
        friendshipStorage.addFriend(userId, friendId, FriendshipStatus.UNCONFIRMED);
        log.info("Пользователь {} отправил заявку в друзья пользователю {}", userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);

        friendshipStorage.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }


    public List<User> getFriends(long userId) {
        getUserById(userId);
        Set<Long> friendIds = friendshipStorage.getFriends(userId).keySet();
        return friendIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        Set<Long> set1 = friendshipStorage.getFriends(userId).keySet();
        Set<Long> set2 = friendshipStorage.getFriends(otherUserId).keySet();
        Set<Long> common = new HashSet<>(set1);
        common.retainAll(set2);
        return common.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: логин содержит пробелы: '{}'", user.getLogin());
            throw new ValidationException("Логин не должен содержать пробелов");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: некорректный email: '{}'", user.getEmail());
            throw new ValidationException("Некорректный email");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: дата рождения в будущем: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
