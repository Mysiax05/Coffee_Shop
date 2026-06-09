package com.dbproject.backend.web;

import com.dbproject.backend.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;

public final class SessionUtils {

    public static final String CUSTOMER_ID = "customerId";

    private SessionUtils() {
    }

    public static Integer requireCustomerId(HttpSession session) {
        Object id = session.getAttribute(CUSTOMER_ID);
        if (id == null) {
            throw new UnauthorizedException("Not logged in");
        }
        return (Integer) id;
    }
}
