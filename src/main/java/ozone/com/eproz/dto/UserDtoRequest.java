package ozone.com.eproz.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDtoRequest(
        @NotBlank(message = "Username is required")
        @Size(max = 120, message = "Username must be less than or equal to 120 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Size(max = 50, message = "Email must be less than or equal to 50 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        String password,

        @NotNull(message = "Role is required")
        UUID role
) {
        @Override
        public String toString() {
                return "UserDtoRequest[username=" + username + ", email=" + email + ", password=[REDACTED], role=" + role + "]";
        }
}