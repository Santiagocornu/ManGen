package com.GenMan.GenMan.Exceptions;

import java.time.LocalDateTime;

public record ErrorMessage(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
}
