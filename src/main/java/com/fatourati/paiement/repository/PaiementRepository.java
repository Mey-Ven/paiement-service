package com.fatourati.paiement.repository;

import com.fatourati.paiement.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepository
        extends JpaRepository<Paiement, String>, JpaSpecificationExecutor<Paiement> {

    boolean existsByReferenceTransaction(String referenceTransaction);
}
