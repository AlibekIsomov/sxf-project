package com.sxf.project.repository;

import com.sxf.project.entity.ProfileCD;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileCDRepository extends JpaRepository<ProfileCD, Long> {

    List<ProfileCD> findByFilialId(Long filialId);

    Page<ProfileCD> findAllByName(String name, Pageable pageable);

    List<ProfileCD> findAllByFilialId(Long id);
}


