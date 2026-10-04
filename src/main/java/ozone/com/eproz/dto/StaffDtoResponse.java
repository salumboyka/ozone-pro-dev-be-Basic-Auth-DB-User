package ozone.com.eproz.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import ozone.com.eproz.enums.GenderEnum;
import ozone.com.eproz.model.StaffModel;

public record StaffDtoResponse(
    UUID id,
    String email,
    String fname,
    String mname,
    String lname,
    GenderEnum gender,
    int age,
    String address,
    String status,
    LocalDate dateOfjoin,
    String position,
    UUID userId,
    Instant createdAt,
    Instant updatedAt
) {
    public static StaffDtoResponse from(StaffModel u) {
        return new StaffDtoResponse(
            u.getId(),
            u.getEmail(),
            u.getFname(),
            u.getMname(),
            u.getLname(),
            u.getGender(),
            u.getAge(),
            u.getAddress(),
            u.getStatus(),
            u.getDateOfjoin(),
            u.getPosition(),
            u.getUser() == null ? null : u.getUser().getId(),
            u.getCreatedAt(),
            u.getUpdatedAt()
        );
    }
}