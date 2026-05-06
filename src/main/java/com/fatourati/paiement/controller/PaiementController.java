package com.fatourati.paiement.controller;

import com.fatourati.paiement.dto.PaiementDTO;
import com.fatourati.paiement.service.PaiementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@Tag(name = "Paiements", description = "Gestion des paiements BMCE Pay")
@SecurityRequirement(name = "bearerAuth")
public class PaiementController {

    private final PaiementService service;

    @GetMapping
    @Operation(summary = "Lister tous les paiements")
    public ResponseEntity<List<PaiementDTO.Response>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un paiement par ID")
    public ResponseEntity<PaiementDTO.Response> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/search")
    @Operation(summary = "Recherche multi-critères (11 champs)")
    public ResponseEntity<List<PaiementDTO.Response>> search(
            @RequestBody PaiementDTO.SearchRequest req) {
        return ResponseEntity.ok(service.search(req));
    }

    @PostMapping
    @Operation(summary = "Enregistrer un nouveau paiement")
    public ResponseEntity<PaiementDTO.Response> create(
            @Valid @RequestBody PaiementDTO.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    @Operation(summary = "Modifier un paiement (Admin uniquement)")
    public ResponseEntity<PaiementDTO.Response> update(
            @PathVariable String id,
            @RequestBody PaiementDTO.UpdateRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    @Operation(summary = "Supprimer un paiement (Admin uniquement)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
