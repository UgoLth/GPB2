package controller;

import modele.Proprietaire;
import modele.Animal;
import service.ApiService;
import java.util.List;

public class ProprietaireController {
    private final ApiService apiService;

    public ProprietaireController() {
        this.apiService = ApiService.getInstance();
    }

    public List<Proprietaire> getProprietaires() {
        try {
            return apiService.getProprietaires();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Alias pour la compatibilité avec le code existant
    public List<Proprietaire> getAllProprietaires() {
        return getProprietaires();
    }

    public boolean ajouterProprietaire(Proprietaire proprietaire) {
        try {
            return apiService.ajouterProprietaire(proprietaire);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifierProprietaire(Proprietaire proprietaire) {
        try {
            return apiService.modifierProprietaire(proprietaire);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimerProprietaire(int id) {
        try {
            return apiService.supprimerProprietaire(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Animal> getAnimauxProprietaire(int proprietaireId) {
        try {
            return apiService.getAnimaux(proprietaireId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean ajouterAnimal(Animal animal) {
        try {
            return apiService.ajouterAnimal(animal);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifierAnimal(Animal animal) {
        try {
            return apiService.modifierAnimal(animal);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimerAnimal(int id) {
        try {
            return apiService.supprimerAnimal(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
