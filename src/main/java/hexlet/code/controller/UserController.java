package hexlet.code.controller;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    ResponseEntity<List<UserDTO>> index() {
        var users = userService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    @GetMapping("/{id}")
    UserDTO show(@PathVariable Long id) throws ResourceNotFoundException {
        return userService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDTO create(@Valid @RequestBody UserCreateDTO createDTO) throws ResourceAlreadyExistsException {
        return userService.create(createDTO);
    }

    @PutMapping("/{id}")
    UserDTO update(@Valid @RequestBody UserUpdateDTO updateDTO, @PathVariable Long id)
            throws ResourceNotFoundException, ResourceAlreadyExistsException {
        return userService.update(id, updateDTO);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
