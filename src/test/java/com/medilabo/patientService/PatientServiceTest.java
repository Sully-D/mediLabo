package com.medilabo.patientService;

import com.medilabo.patientService.Util.PatientValidationService;
import com.medilabo.patientService.model.Patient;
import com.medilabo.patientService.repository.PatientRepository;
import com.medilabo.patientService.service.PatientService;
import com.medilabo.patientService.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientValidationService patientValidationService;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth("01/01/1990");
    }

    @Test
    void testAddPatient_Success() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient savedPatient = patientService.add(patient);

        verify(patientValidationService, times(1)).validatePatient(patient);
        verify(patientRepository, times(1)).save(patient);
        assertEquals(patient, savedPatient);
    }

    @Test
    void testAddPatient_ValidationFails() {
        doThrow(new IllegalArgumentException("Invalid patient data")).when(patientValidationService).validatePatient(any(Patient.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> patientService.add(patient));

        assertEquals("Validation failed: Invalid patient data", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void testAddPatient_DatabaseError() {
        when(patientRepository.save(any(Patient.class))).thenThrow(new DataAccessException("DB error") {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> patientService.add(patient));

        assertEquals("Database operation failed: DB error", exception.getMessage());
        verify(patientValidationService, times(1)).validatePatient(patient);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void testUpdatePatient_Success() {
        when(patientRepository.findByFirstNameAndLastNameAndDateOfBirth(anyString(), anyString(), anyString())).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient updatedPatient = patientService.update(patient);

        verify(patientValidationService, times(1)).validatePatient(patient);
        verify(patientRepository, times(1)).findByFirstNameAndLastNameAndDateOfBirth(patient.getFirstName(), patient.getLastName(), patient.getDateOfBirth());
        verify(patientRepository, times(1)).save(patient);
        assertEquals(patient, updatedPatient);
    }

    @Test
    void testUpdatePatient_NotFound() {
        when(patientRepository.findByFirstNameAndLastNameAndDateOfBirth(anyString(), anyString(), anyString())).thenReturn(Optional.empty());

        Patient result = patientService.update(patient);

        verify(patientValidationService, times(1)).validatePatient(patient);
        verify(patientRepository, times(1)).findByFirstNameAndLastNameAndDateOfBirth(patient.getFirstName(), patient.getLastName(), patient.getDateOfBirth());
        verify(patientRepository, never()).save(any(Patient.class));
        assertNull(result);
    }

    @Test
    void testGetAllPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<Patient> patients = patientService.getAll();

        verify(patientRepository, times(1)).findAll();
        assertFalse(patients.isEmpty());
    }

    @Test
    void testFindOneByFirstNameAndLastName_Success() {
        when(patientRepository.findByFirstNameAndLastName(anyString(), anyString())).thenReturn(Optional.ofNullable(patient));

        Optional<Patient> result = patientService.findOneByFirstNameAndLastName("John", "Doe");

        verify(patientValidationService, times(1)).isNameEmpty("John", "Doe");
        verify(patientRepository, times(1)).findByFirstNameAndLastName("John", "Doe");
        assertTrue(result.isPresent());
        assertEquals(patient, result.get());
    }

    @Test
    void testFindOneByFirstNameAndLastName_ValidationFails() {
        doThrow(new IllegalArgumentException("Names cannot be empty")).when(patientValidationService).isNameEmpty(anyString(), anyString());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> patientService.findOneByFirstNameAndLastName("", ""));

        assertEquals("Validation failed: Names cannot be empty", exception.getMessage());
        verify(patientRepository, never()).findByFirstNameAndLastName(anyString(), anyString());
    }

    @Test
    void testDeletePatient_Success() {
        doNothing().when(patientRepository).deleteById(anyString());

        patientService.delete("123");

        verify(patientRepository, times(1)).deleteById("123");
    }
}
