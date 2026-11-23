package com.ua.rsz.web.dto;

import java.util.List;

/**
 * The one shape every error response has.
 * Example: {"status":404,"error":"Not Found","message":"...","details":[]}
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        List<String> details
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, List.of());
    }
}
