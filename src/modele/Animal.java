package modele;

import java.util.Date;

public class Animal {
    private int id;
    private String nom;
    private String race;
    private Date dateNaissance;
    private int especeId;
    private int proprietaireId;
    private String especeNom; // Pour stocker le nom de l'espèce reçu de l'API

    public Animal() {
    }

    public Animal(int id, String nom, String race, Date dateNaissance, int especeId, int proprietaireId, String especeNom) {
        this.id = id;
        this.nom = nom;
        this.race = race;
        this.dateNaissance = dateNaissance;
        this.especeId = especeId;
        this.proprietaireId = proprietaireId;
        this.especeNom = especeNom;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getRace() {
        return race;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public int getEspeceId() {
        return especeId;
    }

    public int getProprietaireId() {
        return proprietaireId;
    }

    public String getEspeceNom() {
        return especeNom;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public void setEspeceId(int especeId) {
        this.especeId = especeId;
    }

    public void setProprietaireId(int proprietaireId) {
        this.proprietaireId = proprietaireId;
    }

    public void setEspeceNom(String especeNom) {
        this.especeNom = especeNom;
    }
}
