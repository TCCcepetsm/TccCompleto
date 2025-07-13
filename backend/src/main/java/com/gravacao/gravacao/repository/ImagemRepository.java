package com.gravacao.gravacao.repository;

import com.gravacao.gravacao.model.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, Long> {

    /**
     * Busca imagens por tipo de evento
     */
    List<Imagem> findByTipoEventoIgnoreCase(String tipoEvento);

    /**
     * Busca imagens por data do evento
     */
    List<Imagem> findByDataEvento(LocalDate dataEvento);

    /**
     * Busca imagens por período de data do evento
     */
    List<Imagem> findByDataEventoBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca imagens ordenadas por data de upload (mais recentes primeiro)
     */
    List<Imagem> findAllByOrderByDataUploadDesc();

    /**
     * Busca imagens por tipo de evento ordenadas por data de upload
     */
    List<Imagem> findByTipoEventoIgnoreCaseOrderByDataUploadDesc(String tipoEvento);

    /**
     * Busca imagens recentes (últimos N registros)
     */
    @Query("SELECT i FROM Gravacao i ORDER BY i.dataUpload DESC")
    List<Imagem> findRecentImages(@Param("limit") int limit);

    /**
     * Busca imagens por título (busca parcial, case insensitive)
     */
    List<Imagem> findByTituloContainingIgnoreCase(String titulo);

    /**
     * Conta imagens por tipo de evento
     */
    long countByTipoEventoIgnoreCase(String tipoEvento);
}
