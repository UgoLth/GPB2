package controller;

import modele.Tarification;
import service.ApiService;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class HebergementController {
    private static final Logger LOGGER = Logger.getLogger(HebergementController.class.getName());
    private ApiService apiService;

    public HebergementController() {
        this.apiService = ApiService.getInstance();
    }

    public List<Tarification> getTarifications(int pensionId) {
        try {
            List<Tarification> tarifications = apiService.getTarifications(pensionId);
            if (tarifications == null) {
                LOGGER.warning("La récupération des tarifications a échoué pour le pensionId " + pensionId);
                return null;
            }
            LOGGER.info("Nombre de tarifications récupérées pour le pensionId " + pensionId + " : " + tarifications.size());
            return tarifications;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des tarifications pour le pensionId " + pensionId, e);
            return null;
        }
    }

    public boolean ajouterTarification(Tarification tarification) {
        try {
            boolean result = apiService.ajouterTarification(tarification);
            if (result) {
                LOGGER.info("Tarification ajoutée avec succès pour l'id " + tarification.getId());
            } else {
                LOGGER.warning("Échec de l'ajout de la tarification pour l'id " + tarification.getId());
            }
            return result;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de la tarification pour l'id " + tarification.getId(), e);
            return false;
        }
    }

    public boolean modifierTarification(Tarification tarification) {
        try {
            boolean result = apiService.modifierTarification(tarification);
            if (result) {
                LOGGER.info("Tarification modifiée avec succès pour l'id " + tarification.getId());
            } else {
                LOGGER.warning("Échec de la modification de la tarification pour l'id " + tarification.getId());
            }
            return result;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification de la tarification pour l'id " + tarification.getId(), e);
            return false;
        }
    }

    public boolean supprimerTarification(int id) {
        try {
            boolean result = apiService.supprimerTarification(id);
            if (result) {
                LOGGER.info("Tarification supprimée avec succès pour l'id " + id);
            } else {
                LOGGER.warning("Échec de la suppression de la tarification pour l'id " + id);
            }
            return result;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la tarification pour l'id " + id, e);
            return false;
        }
    }
}
