package modele;

import com.google.gson.annotations.SerializedName;

public class Box {
    private int id;
    private String numero;
    private String taille;
    private String type;
    private boolean disponibilite;
    @SerializedName("tarification_id")
    private int tarificationId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public Box() {
        // Constructeur par défaut
        this.disponibilite = true; // Par défaut, un nouveau box est disponible
    }

    public Box(int id, String numero, String taille, String type, boolean disponibilite, int tarificationId) {
        this.id = id;
        this.numero = numero;
        this.taille = taille;
        this.type = type;
        this.disponibilite = disponibilite;
        this.tarificationId = tarificationId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public int getTarificationId() {
        return tarificationId;
    }

    public void setTarificationId(int tarificationId) {
        this.tarificationId = tarificationId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
