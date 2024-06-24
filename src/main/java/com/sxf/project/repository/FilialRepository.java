package com.sxf.project.repository;

import com.sxf.project.entity.Filial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface FilialRepository extends JpaRepository<Filial, Long> {
    Page<Filial> findAllByNameContains(String name, Pageable pageable);

    List<Filial> findByCreatedAtBetween(Instant startDate, Instant endDate);



    @Query("SELECT f FROM Filial f LEFT JOIN FETCH f.managers WHERE f.id = :filialId")
    Optional<Filial> findByIdWithManagers(@Param("filialId") Long filialId);

}
