package com.sxf.project.repository;

import com.sxf.project.entity.ProfilePD;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfilePDRepository extends JpaRepository<ProfilePD, Long> {

    List<ProfilePD> findByFilialId(Long filialId);

    Page<ProfilePD> findAllByName(String name, Pageable pageable);

    List<ProfilePD> findAllByFilialId(Long id);
}
