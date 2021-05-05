package me.ohtaeg.securitystudy.controller;

import javax.validation.Valid;
import me.ohtaeg.securitystudy.controller.dto.UserRequest;
import me.ohtaeg.securitystudy.entity.User;
import me.ohtaeg.securitystudy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/users")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> signup(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.signup(userRequest));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<User> getUser() {
        return ResponseEntity.ok(userService.getMyUserWithAuthorities().get());
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<User> getUserForAdmin(@PathVariable final String username) {
        return ResponseEntity.ok(userService.getUserWithAuthorities(username).get());
    }
}
