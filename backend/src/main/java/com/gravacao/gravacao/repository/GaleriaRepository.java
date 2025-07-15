package com.gravacao.gravacao.repository;

import com.gravacao.controller.entity.Galeria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GaleriaRepository extends JpaRepository<Galeria, Long> {

    /**
     * Busca imagens por tipo de evento
     */
    List<Galeria> findByTipoEventoIgnoreCase(String tipoEvento);

    /**
     * Busca imagens por data do evento
     */
    List<Galeria> findByDataEvento(LocalDate dataEvento);

    /**
     * Busca imagens por período de data do evento
     */
    List<Galeria> findByDataEventoBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca imagens ordenadas por data de upload (mais recentes primeiro)
     */
    List<Galeria> findAllByOrderByDataUploadDesc();

    /**
     * Busca imagens por tipo de evento ordenadas por data de upload
     */
    List<Galeria> findByTipoEventoIgnoreCaseOrderByDataUploadDesc(String tipoEvento);

    /**
     * Busca imagens recentes (últimos N registros)
     */
      @Query(value = "SELECT i FROM Galeria i ORDER BY i.dataUpload DESC LIMIT :limit")
    List<Galeria> findRecentImages(@Param("limit") int limit);

    /**
     * Busca imagens por título (busca parcial, case insensitive)
     */
    List<Galeria> findByTituloContainingIgnoreCase(String titulo);

    /**
     * Conta imagens por tipo de evento
     */
    long countByTipoEventoIgnoreCase(String tipoEvento);
}
