package com.sxf.project.repository;

import com.sxf.project.entity.ProfilePD;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePDRepository extends JpaRepository<ProfilePD,Long> {


    Page<ProfilePD> findAllByName(String name, Pageable pageable);
}
