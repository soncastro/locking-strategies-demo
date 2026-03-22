package dev.soncastro.lockingstrategiesdemo.persistence.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.soncastro.lockingstrategiesdemo.persistence.entity.AccountEntity;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {

    @Modifying
    @Query(value ="""
        UPDATE account a
        SET balance = :balance
        WHERE 
        a.id = :accountId
    """, nativeQuery = true)
    int resetBalance(@Param("accountId") Long accountId, @Param("balance") BigDecimal balance);  

    @Query(value = """
        SELECT a.balance FROM account a
        WHERE 
        a.id = :accountId
        FOR UPDATE
    """, nativeQuery = true)
    Optional<BigDecimal> findBalanceByIdWithPessimisticLock(@Param("accountId") Long accountId);

    @Query(value = """
        SELECT a.balance FROM account a
        WHERE 
        a.id = :accountId
    """, nativeQuery = true)
    Optional<BigDecimal> findBalanceById(@Param("accountId") Long accountId);    

    @Query(value = """
        SELECT 1 FROM account a
        WHERE 
        a.id = :accountId
        FOR UPDATE
    """, nativeQuery = true)
    void findForUpdate(@Param("accountId") Long accountId);    

    @Modifying
    @Query(value = """
        UPDATE account a
        SET 
        balance = :balance
        WHERE 
        a.id = :accountId
    """, nativeQuery = true)
    int updateBalanceById(@Param("accountId") Long accountId, @Param("balance") BigDecimal balance);

    @Modifying
    @Query(value = """
        UPDATE account a
        SET 
        balance = :balance,
        version = version + 1
        WHERE 
        a.id = :accountId
        AND a.version = :version
    """, nativeQuery = true)
    int updateBalanceByIdAndVersion(@Param("accountId") Long accountId, @Param("balance") BigDecimal balance, @Param("version") Long version);    

    @Query(value ="""
        SELECT a.version FROM account a
        WHERE 
        a.id = :accountId
    """, nativeQuery = true)
    Optional<Long> findVersionById(@Param("accountId") Long accountId);

}
