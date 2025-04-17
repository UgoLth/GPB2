package controller;

import modele.Pension;
import service.ApiService;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PensionController {
    private static final Logger LOGGER = Logger.getLogger(PensionController.class.getName());
    private ApiService apiService;

    public PensionController() {
        this.apiService = ApiService.getInstance();
    }

    public List<Pension> getAllPensions() {
        try {
            return apiService.getPensions();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting pensions", e);
            return new ArrayList<>();
        }
    }

    public boolean ajouterPension(Pension pension) {
        try {
            return apiService.ajouterPension(pension);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error adding pension", e);
            return false;
        }
    }

    public boolean modifierPension(Pension pension) {
        try {
            return apiService.modifierPension(pension);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error updating pension", e);
            return false;
        }
    }

    public boolean supprimerPension(int id) {
        try {
            return apiService.supprimerPension(id);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting pension", e);
            return false;
        }
    }
}
