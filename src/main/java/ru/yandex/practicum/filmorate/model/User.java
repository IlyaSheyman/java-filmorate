package ru.yandex.practicum.filmorate.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Builder
@Data
@ApiModel(description = "Пользователь")
public class User {
    private int id;
    @ApiModelProperty(value = "почта", example = "ilyasheiman@yandex.ru") // аннотация для apiDocumentation и swagger, не указывал везде
    @Email
    private String email;
    @NotEmpty
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
