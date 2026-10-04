package ozone.com.eproz.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import ozone.com.eproz.model.UserModel;

public record UserDtoResponse(
        UUID id,
        String username,
        String email,
        Set<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserDtoResponse from(UserModel user) {
        Set<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toUnmodifiableSet());

        return new UserDtoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}