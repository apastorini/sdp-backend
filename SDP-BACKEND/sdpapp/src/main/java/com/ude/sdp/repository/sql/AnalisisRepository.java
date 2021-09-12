package com.ude.sdp.repository.sql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ude.sdp.model.Analisis;


@Repository
public interface AnalisisRepository extends JpaRepository<Analisis, Long> {
	
	public Analisis findById(long id);
	
	public List<Analisis> findByIdUser(long idUser);
	
	@Query("SELECT p FROM Analisis p  WHERE p.idUser=:idUser and p.idFile=:idFile")
	public List<Analisis> findByIdUserAndIdFile(@Param("idUser") long idUser,@Param("idFile") long idFile);
	
	@Query("SELECT p FROM Analisis p  WHERE p.procesado = false ORDER BY  fechaInicial ASC")
	public List<Analisis> findByNoProcesado();
	
	@Query("SELECT p FROM Analisis p  WHERE p.procesado = false and p.email=:email ORDER BY  fechaInicial ASC")
	public List<Analisis> findByNoProcesadoByEmail(@Param("email") String email);
	
	
	@Query("SELECT p FROM Analisis p  WHERE p.procesado = true and p.email=:email and p.numMatches>0 ")
	public List<Analisis> findFilesPlagied(@Param("email") String email);
	
	public List<Analisis> findByEmail(String email);
}
