package com.auction.security;

import com.auction.model.User;

public interface SecurityContext {
    User getCurrentUser();
}