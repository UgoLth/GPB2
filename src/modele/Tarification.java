package modele;

import com.google.gson.annotations.SerializedName;

public class Tarification {
    private int id;
    @SerializedName("TypeGardiennage_id")
    private int typeGardiennageId;
    @SerializedName("Tarif")
    private double tarif;
    @SerializedName("pension_id")
    private int pensionId;

    // Constructeur par défaut
    public Tarification() {}

    // Constructeur avec paramètres
    public Tarification(int id, int typeGardiennageId, double tarif, int pensionId) {
        this.id = id;
        this.typeGardiennageId = typeGardiennageId;
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
        return "Tarification { id=" + id + ", typeGardiennageId=" + typeGardiennageId + ", tarif=" + tarif + ", pensionId=" + pensionId + " }";
    }
}
