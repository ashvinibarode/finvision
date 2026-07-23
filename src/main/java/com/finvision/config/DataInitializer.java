package com.finvision.config;

import com.finvision.user.entity.Role;
import com.finvision.user.entity.RoleType;
import com.finvision.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        if (roleRepository.findByName(RoleType.ROLE_USER).isEmpty()) {
            roleRepository.save(
                    Role.builder()
                            .name(RoleType.ROLE_USER)
                            .build()
            );
        }

        if (roleRepository.findByName(RoleType.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(
                    Role.builder()
                            .name(RoleType.ROLE_ADMIN)
                            .build()
            );
        }
    }
}