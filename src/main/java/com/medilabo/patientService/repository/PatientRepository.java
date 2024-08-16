package com.medilabo.patientService.repository;

import com.medilabo.patientService.model.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    Optional<Patient> findByFirstNameAndLastName(String firstName, String lastName);

    Optional<Patient> findByFirstNameAndLastNameAndDateOfBirth(String firstName, String lastName, String dateOfBirth);
}
