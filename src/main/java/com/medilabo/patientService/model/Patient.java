package com.medilabo.patientService.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

@Document(collection = "patients")
@Data
public class Patient {

    @MongoId
    private String id;

    @NotNull
    @Size(min = 1, max = 50)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 50)
    private String lastName;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") // YYYY-MM-DD format
    private String dateOfBirth;

    @NotNull
    @Pattern(regexp = "Male|Female")
    private String gender;

    private String postalAddress;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$")
    private String phone;
}
