package com.fatourati.paiement.service;

import com.fatourati.paiement.dto.PaiementDTO;
import com.fatourati.paiement.entity.Paiement;
import com.fatourati.paiement.repository.PaiementRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaiementService {

    private final PaiementRepository repository;
    private final Random random = new Random();

    // ─── Liste complète ────────────────────────────────────────────────────────
    public List<PaiementDTO.Response> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── Détail par ID ─────────────────────────────────────────────────────────
    public PaiementDTO.Response getById(String id) {
        Paiement p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable : " + id));
        return toResponse(p);
    }

    // ─── Recherche multi-critères (11 champs) ──────────────────────────────────
    public List<PaiementDTO.Response> search(PaiementDTO.SearchRequest req) {
        Specification<Paiement> spec = buildSpec(req);
        return repository.findAll(spec)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── Création ──────────────────────────────────────────────────────────────
    @Transactional
    public PaiementDTO.Response create(PaiementDTO.CreateRequest req) {
        // Vérification référence transaction unique
        if (req.getReferenceTransaction() != null &&
                repository.existsByReferenceTransaction(req.getReferenceTransaction())) {
            throw new RuntimeException("Référence transaction déjà utilisée : " + req.getReferenceTransaction());
        }

        Paiement entity = Paiement.builder()
                .idPaiement(generateId())
                .codeCreancier(req.getCodeCreancier())
                .idCreance(req.getIdCreance())
                .referenceFacture(req.getReferenceFacture())
                .referenceArticle(req.getReferenceArticle())
                .ribClient(req.getRibClient())
                .numeroCompte(req.getNumeroCompte())
                .numeroTiers(req.getNumeroTiers())
                .nomClient(req.getNomClient())
                .montant(req.getMontant())
                .contratBat(req.getContratBat())
                .canalPaiement(req.getCanalPaiement())
                .matricule(req.getMatricule())
                .statut(req.getStatut() != null ? req.getStatut() : "EFFECTUE")
                .idClient(req.getIdClient())
                .referenceTransaction(req.getReferenceTransaction())
                .datePaiement(req.getDatePaiement() != null ? req.getDatePaiement() : LocalDateTime.now())
                .build();

        repository.save(entity);
        log.info("✅ Paiement créé : {}", entity.getIdPaiement());
        return toResponse(entity);
    }

    // ─── Modification ──────────────────────────────────────────────────────────
    @Transactional
    public PaiementDTO.Response update(String id, PaiementDTO.UpdateRequest req) {
        Paiement entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable : " + id));

        if (req.getCodeCreancier()       != null) entity.setCodeCreancier(req.getCodeCreancier());
        if (req.getIdCreance()           != null) entity.setIdCreance(req.getIdCreance());
        if (req.getReferenceFacture()    != null) entity.setReferenceFacture(req.getReferenceFacture());
        if (req.getReferenceArticle()    != null) entity.setReferenceArticle(req.getReferenceArticle());
        if (req.getRibClient()           != null) entity.setRibClient(req.getRibClient());
        if (req.getNumeroCompte()        != null) entity.setNumeroCompte(req.getNumeroCompte());
        if (req.getNumeroTiers()         != null) entity.setNumeroTiers(req.getNumeroTiers());
        if (req.getNomClient()           != null) entity.setNomClient(req.getNomClient());
        if (req.getMontant()             != null) entity.setMontant(req.getMontant());
        if (req.getContratBat()          != null) entity.setContratBat(req.getContratBat());
        if (req.getCanalPaiement()       != null) entity.setCanalPaiement(req.getCanalPaiement());
        if (req.getMatricule()           != null) entity.setMatricule(req.getMatricule());
        if (req.getStatut()              != null) entity.setStatut(req.getStatut());
        if (req.getIdClient()            != null) entity.setIdClient(req.getIdClient());
        if (req.getReferenceTransaction() != null) entity.setReferenceTransaction(req.getReferenceTransaction());
        if (req.getDatePaiement()        != null) entity.setDatePaiement(req.getDatePaiement());

        repository.save(entity);
        log.info("✅ Paiement modifié : {}", id);
        return toResponse(entity);
    }

    // ─── Suppression ───────────────────────────────────────────────────────────
    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Paiement introuvable : " + id);
        }
        repository.deleteById(id);
        log.info("🗑️ Paiement supprimé : {}", id);
    }

    // ─── Specification (recherche multi-critères) ──────────────────────────────
    private Specification<Paiement> buildSpec(PaiementDTO.SearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (req.getCodeCreancier() != null && !req.getCodeCreancier().isBlank())
                predicates.add(cb.equal(root.get("codeCreancier"), req.getCodeCreancier()));

            if (req.getIdCreance() != null && !req.getIdCreance().isBlank())
                predicates.add(cb.equal(root.get("idCreance"), req.getIdCreance()));

            if (req.getReferenceFacture() != null && !req.getReferenceFacture().isBlank())
                predicates.add(cb.like(cb.lower(root.get("referenceFacture")),
                        "%" + req.getReferenceFacture().toLowerCase() + "%"));

            if (req.getRibClient() != null && !req.getRibClient().isBlank())
                predicates.add(cb.equal(root.get("ribClient"), req.getRibClient()));

            if (req.getNomClient() != null && !req.getNomClient().isBlank())
                predicates.add(cb.like(cb.lower(root.get("nomClient")),
                        "%" + req.getNomClient().toLowerCase() + "%"));

            if (req.getCanalPaiement() != null && !req.getCanalPaiement().isBlank())
                predicates.add(cb.equal(root.get("canalPaiement"), req.getCanalPaiement()));

            if (req.getStatut() != null && !req.getStatut().isBlank())
                predicates.add(cb.equal(root.get("statut"), req.getStatut()));

            if (req.getMontantMin() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("montant"), req.getMontantMin()));

            if (req.getMontantMax() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("montant"), req.getMontantMax()));

            if (req.getDateDebut() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("datePaiement"), req.getDateDebut()));

            if (req.getDateFin() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("datePaiement"), req.getDateFin()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ─── Mapping entité → DTO ──────────────────────────────────────────────────
    private PaiementDTO.Response toResponse(Paiement p) {
        PaiementDTO.Response r = new PaiementDTO.Response();
        r.setIdPaiement(p.getIdPaiement());
        r.setCodeCreancier(p.getCodeCreancier());
        r.setIdCreance(p.getIdCreance());
        r.setReferenceFacture(p.getReferenceFacture());
        r.setReferenceArticle(p.getReferenceArticle());
        r.setRibClient(p.getRibClient());
        r.setNumeroCompte(p.getNumeroCompte());
        r.setNumeroTiers(p.getNumeroTiers());
        r.setNomClient(p.getNomClient());
        r.setMontant(p.getMontant());
        r.setContratBat(p.getContratBat());
        r.setCanalPaiement(p.getCanalPaiement());
        r.setMatricule(p.getMatricule());
        r.setStatut(p.getStatut());
        r.setIdClient(p.getIdClient());
        r.setReferenceTransaction(p.getReferenceTransaction());
        r.setDatePaiement(p.getDatePaiement());
        return r;
    }

    // ─── Génération ID : PAY-YYYYMMDD-XXXX ────────────────────────────────────
    private String generateId() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seq  = String.format("%04d", random.nextInt(9000) + 1000);
        return "PAY-" + date + "-" + seq;
    }
}
