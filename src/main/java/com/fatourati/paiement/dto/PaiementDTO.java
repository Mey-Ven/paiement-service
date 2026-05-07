package com.fatourati.paiement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaiementDTO {

    // ─── Requête création ──────────────────────────────────────────────────────
    @Data
    public static class CreateRequest {

        @NotBlank(message = "Le code créancier est obligatoire")
        private String codeCreancier;

        @NotBlank(message = "L'identifiant de la créance est obligatoire")
        private String idCreance;
        private String referenceFacture;
        private String referenceArticle;
        private String ribClient;
        private String numeroCompte;
        private String numeroTiers;
        private String nomClient;

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
        private BigDecimal montant;

        private String contratBat;

        @NotBlank(message = "Le canal de paiement est obligatoire")
        private String canalPaiement;   // Daman_Cash | Bank_Al_Karam | BMCE_Direct | Agences_BMCE | Mobile_App

        private String matricule;
        private String statut;          // EFFECTUE (défaut) | EN_ATTENTE | ECHEC | ANNULE
        private Integer idClient;
        private String referenceTransaction;
        private LocalDateTime datePaiement;
    }

    // ─── Requête modification ──────────────────────────────────────────────────
    @Data
    public static class UpdateRequest {
        private String codeCreancier;
        private String idCreance;
        private String referenceFacture;
        private String referenceArticle;
        private String ribClient;
        private String numeroCompte;
        private String numeroTiers;
        private String nomClient;
        private BigDecimal montant;
        private String contratBat;
        private String canalPaiement;
        private String matricule;
        private String statut;
        private Integer idClient;
        private String referenceTransaction;
        private LocalDateTime datePaiement;
    }

    // ─── Critères de recherche multi-critères (11 champs) ─────────────────────
    @Data
    public static class SearchRequest {
        private String codeCreancier;       // 1
        private String idCreance;           // 2
        private String referenceFacture;    // 3 (like)
        private String ribClient;           // 4
        private String nomClient;           // 5 (like)
        private String canalPaiement;       // 6
        private String statut;              // 7
        private BigDecimal montantMin;      // 8
        private BigDecimal montantMax;      // 9
        private LocalDateTime dateDebut;    // 10
        private LocalDateTime dateFin;      // 11
    }

    // ─── Réponse ───────────────────────────────────────────────────────────────
    @Data
    public static class Response {
        private String idPaiement;
        private String codeCreancier;
        private String idCreance;
        private String referenceFacture;
        private String referenceArticle;
        private String ribClient;
        private String numeroCompte;
        private String numeroTiers;
        private String nomClient;
        private BigDecimal montant;
        private String contratBat;
        private String canalPaiement;
        private String matricule;
        private String statut;
        private Integer idClient;
        private String referenceTransaction;
        private LocalDateTime datePaiement;
    }
}
