package com.sxf.project.repository;


import com.sxf.project.entity.TypeOfUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeOfUnitRepository extends JpaRepository<TypeOfUnit, Long> {
}
