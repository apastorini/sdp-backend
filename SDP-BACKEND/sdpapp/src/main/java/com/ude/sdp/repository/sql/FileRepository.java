package com.ude.sdp.repository.sql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ude.sdp.model.File;


@Repository
public interface FileRepository extends JpaRepository<File, Long> {
	
	public File findByPath(String path);
	
	public File findByFileID(long fileID);
	
	public File findByName(String name);
	
	@Query("SELECT p FROM File p  WHERE p.toAnalize = true ")
	public List<File> findAllNotNWordsProcess();
	 
	@Query("SELECT p FROM File p  WHERE p.toPdf = true and p.borrado=false")
	public List<File> findAllNotPdf();
	
	// @Query("SELECT p FROM File p")
	//public List<File> findAll();
	 
	 @Query("SELECT p FROM File p WHERE p.user.email = :email and p.borrado=false")
	 public List<File> findByEmail(@Param("email") String email);
}
