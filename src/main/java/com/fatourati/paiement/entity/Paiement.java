package com.fatourati.paiement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiement")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @Column(name = "id_paiement", length = 20)
    private String idPaiement;

    @Column(name = "code_creancier", nullable = false, length = 4)
    private String codeCreancier;

    @Column(name = "id_creance", length = 10)
    private String idCreance;

    @Column(name = "reference_facture", length = 50)
    private String referenceFacture;

    @Column(name = "reference_article", length = 50)
    private String referenceArticle;

    @Column(name = "rib_client", length = 24)
    private String ribClient;

    @Column(name = "numero_compte", length = 30)
    private String numeroCompte;

    @Column(name = "numero_tiers", length = 30)
    private String numeroTiers;

    @Column(name = "nom_client", length = 100)
    private String nomClient;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "contrat_bat", length = 50)
    private String contratBat;

    @Column(name = "canal_paiement", nullable = false, length = 50)
    private String canalPaiement;

    @Column(length = 30)
    private String matricule;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String statut = "EFFECTUE";

    @Column(name = "id_client")
    private Integer idClient;

    @Column(name = "reference_transaction", length = 50, unique = true)
    private String referenceTransaction;

    @Column(name = "date_paiement", nullable = false)
    @Builder.Default
    private LocalDateTime datePaiement = LocalDateTime.now();
}
