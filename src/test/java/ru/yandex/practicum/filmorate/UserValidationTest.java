package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserValidationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        User user = new User();
        user.setEmail("");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailWhenEmailHasNoAtSymbol() {
        User user = new User();
        user.setEmail("invalidemail.com");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("user login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }


    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("user1");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }

    @Test
    void shouldPassWhenAllFieldsAreValid() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("user1");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
}
