package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerAccount(String loginName, String rawPassword) {
        if (accountRepository.findByLoginName(loginName).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        Account account = new Account();
        account.setLoginName(loginName);
        account.setPassword(passwordEncoder.encode(rawPassword));

        // Assign default USER role with ROLE_ prefix
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    // Create if it doesn't exist (failsafe)
                    Role newRole = new Role();
                    newRole.setName("ROLE_USER");
                    return roleRepository.save(newRole);
                });

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        account.setRoles(roles);

        accountRepository.save(account);
    }
}
