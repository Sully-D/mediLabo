package com.medilabo.patientService.controller;

import com.medilabo.patientService.model.Patient;
import com.medilabo.patientService.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        logger.info("Received request to add a new patient: {}", patient);
        try {
            Patient savedPatient = patientService.add(patient);
            logger.info("Patient added successfully with ID: {}", savedPatient.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
        } catch (RuntimeException e) {
            logger.error("Error occurred while adding patient: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable String id, @RequestBody Patient patient) {
        logger.info("Received request to update patient with ID: {}", id);
        try {
            patient.setId(id);
            Patient updatedPatient = patientService.update(patient);
            if (updatedPatient == null) {
                logger.warn("Patient with ID: {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            logger.info("Patient with ID: {} updated successfully", id);
            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            logger.error("Error occurred while updating patient with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        logger.info("Received request to fetch all patients");
        List<Patient> patients = patientService.getAll();
        logger.info("Fetched {} patients", patients.size());
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/search")
    public ResponseEntity<Patient> getPatientByName(@RequestParam String firstName, @RequestParam String lastName) {
        logger.info("Received request to search patient by name: {} {}", firstName, lastName);
        try {
            Optional<Patient> patient = patientService.findOneByFirstNameAndLastName(firstName, lastName);
            if (patient.isPresent()) {
                logger.info("Patient found: {}", patient.get());
                return ResponseEntity.ok(patient.get());
            } else {
                logger.warn("Patient not found with name: {} {}", firstName, lastName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (RuntimeException e) {
            logger.error("Error occurred while searching for patient with name: {} {}", firstName, lastName, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        logger.info("Received request to delete patient with ID: {}", id);
        try {
            patientService.delete(id);
            logger.info("Patient with ID: {} deleted successfully", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            logger.error("Error occurred while deleting patient with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
