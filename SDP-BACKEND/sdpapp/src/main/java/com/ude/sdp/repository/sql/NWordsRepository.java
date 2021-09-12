package com.ude.sdp.repository.sql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.ude.sdp.model.NWords;

@Repository
public interface NWordsRepository extends JpaRepository<NWords, Long> {
	
	 @Query("SELECT p FROM NWords p  WHERE p.hash = :hash AND p.file.fileID != :fileID ORDER BY fileID,pageNum,ord ")
	 public List<NWords> findByCustomHash(@Param("hash") String hash,@Param("fileID") long fileID);
	 
	 @Query("SELECT p FROM NWords p  WHERE p.file.fileID = :fileID ORDER BY pageNum,ord ")
	 public List<NWords> findByFileId(@Param("fileID") long  fileID);
	 
	 
	 //obtiene los nwords que tienen el hash pasado en el archivo indicador
	 @Query("SELECT p FROM NWords p  WHERE p.hash = :hash AND p.file.fileID = :fileID ORDER BY pageNum,ord ")
	 public List<NWords> findByHashAndFile(@Param("hash") String hash,@Param("fileID") long fileID);
	
}
