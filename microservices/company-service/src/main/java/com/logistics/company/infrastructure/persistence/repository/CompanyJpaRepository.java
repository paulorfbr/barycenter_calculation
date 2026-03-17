package com.logistics.company.infrastructure.persistence.repository;

import com.logistics.company.infrastructure.persistence.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link CompanyEntity}.
 *
 * All query methods are derived from method names or explicit JPQL.  No
 * native SQL is used here; raw SQL lives in Flyway migration scripts only.
 */
@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, String> {

    boolean existsByName(String name);

    List<CompanyEntity> findByStatus(CompanyEntity.CompanyStatusEnum status);

    @Query("SELECT c FROM CompanyEntity c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<CompanyEntity> searchByNameContaining(@Param("term") String term);
}
