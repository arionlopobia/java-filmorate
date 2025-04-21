package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class User {
    private Long id;

    @NotBlank
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелов")
    private String login;

    @NotBlank
    private String name;

    @PastOrPresent
    private LocalDate birthday;
}
