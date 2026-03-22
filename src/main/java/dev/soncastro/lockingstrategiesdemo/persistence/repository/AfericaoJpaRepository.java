package dev.soncastro.lockingstrategiesdemo.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.soncastro.lockingstrategiesdemo.persistence.entity.AfericaoEntity;

public interface AfericaoJpaRepository extends JpaRepository<AfericaoEntity, Long> {

    @Query(value = """
        SELECT a.vida FROM afericao a
        WHERE
        a.pneu_id = :pneuId
        ORDER BY a.id DESC
        LIMIT 1
    """, nativeQuery = true)
    Integer findLastVidaByPneuId(@Param("pneuId") long pneuId);

    @Modifying
    @Query(value = """
        INSERT INTO afericao 
        (pneu_id, vida)
        VALUES 
        (:pneuId, :vida)
    """, nativeQuery = true)
    void insertAfericao(@Param("pneuId") long pneuId, @Param("vida") int vida);

}
