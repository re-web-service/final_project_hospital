package com.re.hospital.repositories;

import com.re.hospital.entities.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
}
