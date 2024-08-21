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
public interface PatientService {

    Patient add (Patient patient);

    Patient update (Patient patient);

    List<Patient> getAll ();

    Optional<Patient> findOneByFirstNameAndLastName(String firstName, String lastName);

    void delete (String id);

}
