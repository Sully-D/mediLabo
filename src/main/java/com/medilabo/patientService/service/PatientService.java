package com.medilabo.patientService.service;

import com.medilabo.patientService.Util.PatientValidationService;
import com.medilabo.patientService.model.Patient;
import com.medilabo.patientService.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientValidationService patientValidationService;

    @Transactional
    public Patient add (Patient patient) {

        try {
            patientValidationService.validatePatient(patient);

            return patientRepository.save(patient);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed : " + e.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException("Database operation failed : " + e.getMessage());
        }
    }

    @Transactional
    public Patient update (Patient patient) {
        try {
            patientValidationService.validatePatient(patient);

            Optional<Patient> oldPatientEntry = patientRepository.findByFirstNameAndLastNameAndDateOfBirth(
                    patient.getFirstName(), patient.getLastName(), patient.getDateOfBirth()
            );

            if (oldPatientEntry.isEmpty()) {
                System.out.println("Patient not found !");
                return null;
            }

            patient.setId(oldPatientEntry.get().getId());

            return patientRepository.save(patient);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed : " + e.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException("Database operation failed : " + e.getMessage());
        }
    }

    public List<Patient> getAll () {
        return patientRepository.findAll();
    }

    public Optional<Patient> findOneByFirstNameAndLastName(String firstName, String lastName) {

        try {
            patientValidationService.isNameEmpty(firstName, lastName);

            return patientRepository.findByFirstNameAndLastName(firstName, lastName).stream().findFirst();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed : " + e.getMessage());
        }
    }

    public void delete (String id) {
        patientRepository.deleteById(id);
    }

}
