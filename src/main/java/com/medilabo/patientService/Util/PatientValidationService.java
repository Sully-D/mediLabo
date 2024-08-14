package com.medilabo.patientService.Util;

import com.medilabo.patientService.model.Patient;
import org.springframework.stereotype.Service;

@Service
public class PatientValidationService {

    public void validatePatient (Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient object cannot be null");
        }
    }

    public void isNameEmpty (String firstName, String lastName) {
        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("FirstName and LastName can't empty");
        }
    }
}
