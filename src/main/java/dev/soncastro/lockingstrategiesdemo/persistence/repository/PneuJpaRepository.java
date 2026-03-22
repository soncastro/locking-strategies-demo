package dev.soncastro.lockingstrategiesdemo.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.soncastro.lockingstrategiesdemo.persistence.entity.PneuEntity;

public interface PneuJpaRepository extends JpaRepository<PneuEntity, Long> {

    @Query(value = """
        SELECT p.id FROM pneu p
        WHERE
        p.id = :pneuId
        FOR UPDATE
    """, nativeQuery = true)
    Long findByIdForUpdate(@Param("pneuId") long pneuId);

}
