package com.auction.security.impl;

import com.auction.model.Role;
import com.auction.model.User;
import com.auction.security.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextImpl implements SecurityContext {
    
    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        // Return guest user if no authentication found
        User guest = new User();
        guest.setRole(Role.GUEST);
        return guest;
    }
}