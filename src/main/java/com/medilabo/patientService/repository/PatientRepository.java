package com.medilabo.patientService.repository;

import com.medilabo.patientService.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByFirstNameAndLastName(String firstName, String lastName);

    Optional<Patient> findByNameAndDateOfBirth (String firstName, String lastName, String dateOfBirth);
}
