package com.sxf.project.repository;


import com.sxf.project.entity.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Page<Worker> findAllByNameAndSurnameContains(String name, String surname, Pageable pageable);
}
