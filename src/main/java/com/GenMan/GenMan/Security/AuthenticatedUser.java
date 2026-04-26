package com.GenMan.GenMan.Security;

public record AuthenticatedUser(Long userId, String email, String role, Long sucursalId) {
}
