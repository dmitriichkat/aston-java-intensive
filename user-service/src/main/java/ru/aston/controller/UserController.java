package ru.aston.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aston.assembler.UserModelAssembler;
import ru.aston.dto.CreateUserRequest;
import ru.aston.dto.UpdateUserRequest;
import ru.aston.dto.UserResponse;
import ru.aston.exception.ErrorResponse;
import ru.aston.service.UserService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Операции c пользователями")
public class UserController {
    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    public UserController(UserService userService, UserModelAssembler userModelAssembler) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
    }

    @Operation(summary = "Получить список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список всех пользователей")
    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> getAllUsers() {
        List<EntityModel<UserResponse>> users = userService.findAll().stream().map(userModelAssembler::toModel).toList();
        return CollectionModel.of(users, linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }

    @Operation(summary = "Получить пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public EntityModel<UserResponse> getById(
            @Parameter(description = "Id пользователя", example = "1") @PathVariable Long id) {
        return userModelAssembler.toModel(userService.findById(id));
    }

    @Operation(summary = "Создать пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        EntityModel<UserResponse> created = userModelAssembler.toModel(userService.saveUser(createUserRequest));
        return ResponseEntity.created(created.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(created);
    }

    @Operation(summary = "Обновить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public EntityModel<UserResponse> updateUser(
            @Parameter(description = "Id пользователя", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userModelAssembler.toModel(userService.updateUser(id, updateUserRequest));
    }

    @Operation(summary = "Удалить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter (description = "Id пользователя", example = "1") @PathVariable Long id) {
        userService.deleteUser(id);
    }

}
