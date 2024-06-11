package com.sxf.project.repository;



import com.sxf.project.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {


}
