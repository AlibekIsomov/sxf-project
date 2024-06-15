package com.sxf.project.repository;



import com.sxf.project.entity.FileEntity;
import com.sxf.project.entity.Filial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;


@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

}
