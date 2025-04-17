package modele;

public class TypeGardiennage {
    private int id;
    private String libelle; // Hôtel Canin, Camping Canin, Pension Féline

    // Constructeur par défaut
    public TypeGardiennage() {}

    // Constructeur avec paramètres
    public TypeGardiennage(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    // Méthode toString pour affichage
    @Override
    public String toString() {
        return "TypeGardiennage{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
