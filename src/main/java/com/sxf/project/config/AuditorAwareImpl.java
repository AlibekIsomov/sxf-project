package com.sxf.project.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Return a default value, for example, "system" or any default user/identifier
        return Optional.of("ADMIN");
    }

}
