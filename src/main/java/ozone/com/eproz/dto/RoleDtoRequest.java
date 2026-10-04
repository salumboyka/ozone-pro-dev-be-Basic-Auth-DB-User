package ozone.com.eproz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleDtoRequest(
        @NotBlank(message = "Role name is required")
        @Size(max = 50, message = "Role name must be less than or equal to 50 characters")
        String name
) {
}