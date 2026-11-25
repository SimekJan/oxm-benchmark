package queries.northwind.params;

import java.time.LocalDate;

public record CQ7_Params(
    Character firstNameStartingLetter,
    Character lastNameStartingLetter,
    String cityName,
    LocalDate hireDate,
    LocalDate birthDate
) {
}
