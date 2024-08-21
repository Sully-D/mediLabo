package com.medilabo.patientService.service.impl;

import com.medilabo.patientService.Util.PatientValidationService;
import com.medilabo.patientService.model.Patient;
import com.medilabo.patientService.repository.PatientRepository;
import com.medilabo.patientService.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class patientServiceImpl implements PatientService {
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientValidationService patientValidationService;

    @Transactional
    @Override
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
    @Override
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

    @Override
    public List<Patient> getAll () {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> findOneByFirstNameAndLastName(String firstName, String lastName) {

        try {
            patientValidationService.isNameEmpty(firstName, lastName);

            return patientRepository.findByFirstNameAndLastName(firstName, lastName).stream().findFirst();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed : " + e.getMessage());
        }
    }

    @Override
    public void delete (String id) {
        patientRepository.deleteById(id);
    }
}
