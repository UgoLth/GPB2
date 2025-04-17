package modele;

import com.google.gson.annotations.SerializedName;

public class Hebergement {
    private int id;
    @SerializedName("TypeGardiennage_id")
    private int typeGardiennageId;
    @SerializedName("Description")
    private String description;
    @SerializedName("Tarif")
    private double tarif;
    @SerializedName("PensionId")
    private int pensionId;

    // Constructeur par défaut
    public Hebergement() {}

    // Constructeur avec paramètres
    public Hebergement(int id, int typeGardiennageId, String description, double tarif, int pensionId) {
        this.id = id;
        this.typeGardiennageId = typeGardiennageId;
        this.description = description;
        this.tarif = tarif;
        this.pensionId = pensionId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeGardiennageId() {
        return typeGardiennageId;
    }

    public void setTypeGardiennageId(int typeGardiennageId) {
        this.typeGardiennageId = typeGardiennageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTarif() {
        return tarif;
    }

    public void setTarif(double tarif) {
        this.tarif = tarif;
    }

    public int getPensionId() {
        return pensionId;
    }

    public void setPensionId(int pensionId) {
        this.pensionId = pensionId;
    }

    // toString pour debug
    @Override
    public String toString() {
        return "Hebergement { id=" + id + ", typeGardiennageId=" + typeGardiennageId + 
               ", description=" + description + ", tarif=" + tarif + ", pensionId=" + pensionId + " }";
    }
}
