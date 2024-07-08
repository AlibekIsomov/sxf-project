package com.sxf.project.repository;


import com.sxf.project.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByFilialId(Long filialId, Pageable pageable);

    List<Report> findAllByFilialId(Long id);
}
