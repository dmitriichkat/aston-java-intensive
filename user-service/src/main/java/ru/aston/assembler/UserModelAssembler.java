package ru.aston.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.aston.controller.UserController;
import ru.aston.dto.UserResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler <UserResponse, EntityModel <UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(UserResponse userResponse) {
        return EntityModel.of(userResponse,
                linkTo(methodOn(UserController.class).getById(userResponse.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
    }
}
