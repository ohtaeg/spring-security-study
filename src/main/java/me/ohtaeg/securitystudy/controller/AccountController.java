package me.ohtaeg.securitystudy.controller;

import javax.validation.Valid;
import me.ohtaeg.securitystudy.controller.dto.AccountRequest;
import me.ohtaeg.securitystudy.entity.Account;
import me.ohtaeg.securitystudy.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/users")
public class AccountController {

    private final AccountService accountService;

    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> signup(@Valid @RequestBody AccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.signup(accountRequest));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Account> getUser() {
        return ResponseEntity.ok(accountService.getMyUserWithAuthorities().get());
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Account> getUserForAdmin(@PathVariable final String username) {
        return ResponseEntity.ok(accountService.getUserWithAuthorities(username).get());
    }

    @GetMapping("/{role}/{username}/{password}")
    public Account createAccount(@ModelAttribute Account account) {
        return accountService.createNew(account);
    }
}
