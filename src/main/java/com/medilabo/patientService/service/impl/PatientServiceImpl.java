package com.medilabo.patientService.service.impl;

import com.medilabo.patientService.Util.PatientValidationService;
import com.medilabo.patientService.model.Patient;
import com.medilabo.patientService.repository.PatientRepository;
import com.medilabo.patientService.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientValidationService patientValidationService;

    @Transactional
    @Override
    public Patient add(Patient patient) {
        logger.info("Attempting to add a new patient: {}", patient);
        try {
            patientValidationService.validatePatient(patient);
            Patient savedPatient = patientRepository.save(patient);
            logger.info("Patient added successfully with ID: {}", savedPatient.getId());
            return savedPatient;
        } catch (IllegalArgumentException e) {
            logger.error("Validation failed while adding patient: {}", e.getMessage(), e);
            throw new RuntimeException("Validation failed: " + e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Database operation failed while adding patient: {}", e.getMessage(), e);
            throw new RuntimeException("Database operation failed: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public Patient update(Patient patient) {
        logger.info("Attempting to update patient: {}", patient);
        try {
            patientValidationService.validatePatient(patient);

            Optional<Patient> oldPatientEntry = patientRepository.findByFirstNameAndLastNameAndDateOfBirth(
                    patient.getFirstName(), patient.getLastName(), patient.getDateOfBirth()
            );

            if (oldPatientEntry.isEmpty()) {
                logger.warn("Patient not found with the provided details: {} {} {}",
                        patient.getFirstName(), patient.getLastName(), patient.getDateOfBirth());
                return null;
            }

            patient.setId(oldPatientEntry.get().getId());
            Patient updatedPatient = patientRepository.save(patient);
            logger.info("Patient updated successfully with ID: {}", updatedPatient.getId());
            return updatedPatient;
        } catch (IllegalArgumentException e) {
            logger.error("Validation failed while updating patient: {}", e.getMessage(), e);
            throw new RuntimeException("Validation failed: " + e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Database operation failed while updating patient: {}", e.getMessage(), e);
            throw new RuntimeException("Database operation failed: " + e.getMessage());
        }
    }

    @Override
    public List<Patient> getAll() {
        logger.info("Fetching all patients");
        List<Patient> patients = patientRepository.findAll();
        logger.info("Fetched {} patients", patients.size());
        return patients;
    }

    @Override
    public Optional<Patient> findOneByFirstNameAndLastName(String firstName, String lastName) {
        logger.info("Searching for patient by first name: {} and last name: {}", firstName, lastName);
        try {
            patientValidationService.isNameEmpty(firstName, lastName);

            Optional<Patient> patient = patientRepository.findByFirstNameAndLastName(firstName, lastName).stream().findFirst();
            if (patient.isPresent()) {
                logger.info("Patient found: {}", patient.get());
            } else {
                logger.warn("No patient found with the name: {} {}", firstName, lastName);
            }
            return patient;
        } catch (IllegalArgumentException e) {
            logger.error("Validation failed while searching for patient: {}", e.getMessage(), e);
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        logger.info("Attempting to delete patient with ID: {}", id);
        try {
            patientRepository.deleteById(id);
            logger.info("Patient with ID: {} deleted successfully", id);
        } catch (DataAccessException e) {
            logger.error("Database operation failed while deleting patient with ID: {}", id, e);
            throw new RuntimeException("Database operation failed: " + e.getMessage());
        }
    }
}