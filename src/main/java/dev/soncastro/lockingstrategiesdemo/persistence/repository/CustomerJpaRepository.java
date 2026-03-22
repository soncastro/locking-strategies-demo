package dev.soncastro.lockingstrategiesdemo.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.soncastro.lockingstrategiesdemo.persistence.entity.CustomerEntity;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {

    @Query("""
        SELECT (COUNT(c.id) > 0) FROM CustomerEntity c
        WHERE 
        TRIM(UPPER(c.name)) = TRIM(UPPER(:name))
    """)
    boolean existsByName(@Param("name") String name);

}
