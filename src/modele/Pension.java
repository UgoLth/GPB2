package modele;

import com.google.gson.annotations.SerializedName;

public class Pension {
    private int id;
    @SerializedName("Adresse")
    private String adresse;
    @SerializedName("Responsable")
    private String responsable;
    @SerializedName("Telephone")
    private String telephone;
    @SerializedName("Ville")
    private String ville;

    // Constructeur par défaut
    public Pension() {}

    // Constructeur avec paramètres
    public Pension(int id, String adresse, String responsable, String telephone, String ville) {
        this.id = id;
        this.adresse = adresse;
        this.responsable = responsable;
        this.telephone = telephone;
        this.ville = ville;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }
}
