package dev.soncastro.lockingstrategiesdemo.persistence.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.soncastro.lockingstrategiesdemo.persistence.entity.AccountMovementEntity;

public interface AccountMovementJpaRepository extends JpaRepository<AccountMovementEntity, Long> {

    @Modifying
    @Query(value = """
        DELETE FROM account_movement am
        WHERE 
        am.account_id = :accountId
    """, nativeQuery = true)
    int deleteByAccountId(long accountId);

    @Modifying
    @Query(value = """
        INSERT INTO account_movement 
        (account_id, movement_type, amount)
        VALUES 
        (:accountId, :movementType, :amount)
    """, nativeQuery = true)
    int insertAccountMovement(@Param("accountId") long accountId, @Param("movementType") String movementType, @Param("amount") BigDecimal amount);

    @Query("""
        SELECT am
        FROM AccountMovementEntity am
        WHERE 
        am.account.id = :accountId
        ORDER BY am.id ASC
    """)
    List<AccountMovementEntity> findByAccountId(@Param("accountId") long accountId);

}
