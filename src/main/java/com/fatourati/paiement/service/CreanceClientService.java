package com.fatourati.paiement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Client HTTP pour la validation inter-services dans paiement-service.
 *
 *  creancier-service (port 8081) :
 *    - GET /api/creanciers/{code}       → valider un créancier
 *    - GET /api/canaux-paiement         → valider un canal de paiement
 *
 *  creance-service (port 8082) :
 *    - GET /api/creances/{idCreance}    → valider une créance
 *
 * Dégradation gracieuse : si un service est down (ResourceAccessException),
 * la validation est ignorée (warn log) pour ne pas bloquer les paiements.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreanceClientService {

    private final RestTemplate restTemplate;

    /** URL du creancier-service (port 8081) — créanciers + canaux */
    @Value("${creancier.service.url:http://localhost:8081/api}")
    private String creancierServiceUrl;

    /** URL du creance-service (port 8082) — créances */
    @Value("${creance.service.url:http://localhost:8082/api}")
    private String creanceServiceUrl;

    // ── Valider le créancier (creancier-service) ───────────────────────────────
    public void validerCreancier(String codeCreancier) {
        String url = creancierServiceUrl + "/creanciers/" + codeCreancier;
        try {
            restTemplate.getForObject(url, Map.class);
            log.debug("✅ Créancier validé : {}", codeCreancier);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException(
                "Créancier introuvable : '" + codeCreancier + "'. " +
                "Vérifiez le code créancier dans le référentiel."
            );
        } catch (ResourceAccessException e) {
            log.warn("⚠️ creancier-service indisponible — validation créancier ignorée : {}", e.getMessage());
        }
    }

    // ── Valider la créance (creance-service) ──────────────────────────────────
    public void validerCreance(String idCreance) {
        String url = creanceServiceUrl + "/creances/" + idCreance;
        try {
            restTemplate.getForObject(url, Map.class);
            log.debug("✅ Créance validée : {}", idCreance);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException(
                "Créance introuvable : '" + idCreance + "'. " +
                "Vérifiez l'identifiant de créance."
            );
        } catch (ResourceAccessException e) {
            log.warn("⚠️ creance-service indisponible — validation créance ignorée : {}", e.getMessage());
        }
    }

    // ── Valider le canal de paiement (creancier-service) ─────────────────────
    public void validerCanal(String codeCreancier, String nomCanal) {
        String url = creancierServiceUrl + "/canaux-paiement";
        try {
            List<Map> canaux = restTemplate.getForObject(url, List.class);
            if (canaux == null || canaux.isEmpty()) {
                log.warn("⚠️ Aucun canal retourné par creancier-service");
                return;
            }

            boolean canalValide = canaux.stream().anyMatch(canal -> {
                String code     = (String) canal.get("codeCreancier");
                String nom      = (String) canal.get("nomCanal");
                Object actifObj = canal.get("actif");
                boolean actif   = actifObj instanceof Boolean
                    ? (Boolean) actifObj
                    : Boolean.parseBoolean(String.valueOf(actifObj));
                return codeCreancier.equals(code) && nomCanal.equals(nom) && actif;
            });

            if (!canalValide) {
                throw new RuntimeException(
                    "Canal '" + nomCanal + "' invalide ou inactif pour le créancier '" +
                    codeCreancier + "'. Vérifiez la configuration des canaux."
                );
            }
            log.debug("✅ Canal validé : {} pour {}", nomCanal, codeCreancier);

        } catch (RuntimeException e) {
            throw e;   // relancer les erreurs métier
        } catch (ResourceAccessException e) {
            log.warn("⚠️ creancier-service indisponible — validation canal ignorée : {}", e.getMessage());
        }
    }
}
