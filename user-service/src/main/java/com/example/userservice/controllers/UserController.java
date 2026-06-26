package com.example.userservice.controllers;

import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService service;
    private final ModelMapper mapper;

    public UserController(UserService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<UserResponseDTO> list() {
        return service.findAll().stream()
                .map(u -> mapper.map(u, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> get(@PathVariable Long id) {
        User u = service.findById(id);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.map(u, UserResponseDTO.class));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO userDto) {
        User user = mapper.map(userDto, User.class);
        User created = service.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(created, UserResponseDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userDto) {
        User user = mapper.map(userDto, User.class);
        User updated = service.update(id, user);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.map(updated, UserResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

