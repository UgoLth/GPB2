package controller;

import modele.Box;
import service.ApiService;
import java.io.IOException;
import java.util.List;

public class BoxController {
    private final ApiService apiService;

    public BoxController() {
        this.apiService = ApiService.getInstance();
    }

    public List<Box> getBoxes(int tarificationId) throws IOException {
        return apiService.getBoxes(tarificationId);
    }

    public boolean ajouterBox(Box box, int tarificationId) throws IOException {
        return apiService.ajouterBox(box, tarificationId);
    }

    public boolean modifierBox(Box box) throws IOException {
        return apiService.modifierBox(box);
    }

    public boolean supprimerBox(int id) throws IOException {
        return apiService.supprimerBox(id);
    }
}
