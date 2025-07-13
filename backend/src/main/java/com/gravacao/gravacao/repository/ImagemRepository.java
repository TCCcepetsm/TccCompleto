package com.gravacao.backend.repository;

import com.gravacao.backend.model.Gravacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GravacaoRepository extends JpaRepository<Gravacao, Long> {

    /**
     * Busca imagens por tipo de evento
     */
    List<Gravacao> findByTipoEventoIgnoreCase(String tipoEvento);

    /**
     * Busca imagens por data do evento
     */
    List<Gravacao> findByDataEvento(LocalDate dataEvento);

    /**
     * Busca imagens por período de data do evento
     */
    List<Gravacao> findByDataEventoBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca imagens ordenadas por data de upload (mais recentes primeiro)
     */
    List<Gravacao> findAllByOrderByDataUploadDesc();

    /**
     * Busca imagens por tipo de evento ordenadas por data de upload
     */
    List<Gravacao> findByTipoEventoIgnoreCaseOrderByDataUploadDesc(String tipoEvento);

    /**
     * Busca imagens recentes (últimos N registros)
     */
    @Query("SELECT i FROM Gravacao i ORDER BY i.dataUpload DESC")
    List<Gravacao> findRecentImages(@Param("limit") int limit);

    /**
     * Busca imagens por título (busca parcial, case insensitive)
     */
    List<Gravacao> findByTituloContainingIgnoreCase(String titulo);

    /**
     * Conta imagens por tipo de evento
     */
    long countByTipoEventoIgnoreCase(String tipoEvento);
}

