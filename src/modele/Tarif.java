package modele;

public class Tarif {
    private int pensionId;
    private int typeGardiennageId;
    private double tarif;

    public Tarif(int pensionId, int typeGardiennageId, double tarif) {
        this.pensionId = pensionId;
        this.typeGardiennageId = typeGardiennageId;
        this.tarif = tarif;
    }

    // Getters
    public int getPensionId() {
        return pensionId;
    }

    public int getTypeGardiennageId() {
        return typeGardiennageId;
    }

    public double getTarif() {
        return tarif;
    }

    // Setters
    public void setPensionId(int pensionId) {
        this.pensionId = pensionId;
    }

    public void setTypeGardiennageId(int typeGardiennageId) {
        this.typeGardiennageId = typeGardiennageId;
    }

    public void setTarif(double tarif) {
        this.tarif = tarif;
    }
}
