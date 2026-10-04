package ozone.com.eproz.dto;

import java.time.Instant;
import java.util.UUID;

import ozone.com.eproz.model.RoleModel;

public record RoleDtoResponse(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
    public static RoleDtoResponse from(RoleModel role) {
        return new RoleDtoResponse(
                role.getId(),
                role.getName(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }
}