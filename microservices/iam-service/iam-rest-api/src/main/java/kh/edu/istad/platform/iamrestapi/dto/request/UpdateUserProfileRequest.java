package kh.edu.istad.platform.iamrestapi.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserProfileRequest(
        @Size(max = 100)
        String givenName,

        @Size(max = 100)
        String familyName,

        @Size(max = 100)
        String middleName,

        @Size(max = 100)
        String nickname,

        @Size(max = 20)
        String gender,

        LocalDate birthdate,

        @Size(max = 500)
        String profileUrl,

        @Size(max = 500)
        String pictureUrl,

        @Size(max = 500)
        String websiteUrl,

        @Size(max = 50)
        String zoneinfo,

        @Size(max = 10)
        String locale,

        @Size(max = 20)
        String phoneNumber,

        @Size(max = 255)
        String streetAddress,

        @Size(max = 100)
        String locality,

        @Size(max = 100)
        String region,

        @Size(max = 20)
        String postalCode,

        @Size(max = 2)
        String country
) {
}
