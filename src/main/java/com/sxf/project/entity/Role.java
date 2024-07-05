package com.sxf.project.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    MANAGER,
    USER;

    @Override
    public String getAuthority() {
        return "";
    }
}