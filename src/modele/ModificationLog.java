package modele;

import java.time.LocalDateTime;

public class ModificationLog {
    private int idLog;
    private int idPension;
    private LocalDateTime dateModification;
    private String descriptionModification;

    // Constructeur
    public ModificationLog(int idLog, int idPension, LocalDateTime dateModification, String descriptionModification) {
        this.idLog = idLog;
        this.idPension = idPension;
        this.dateModification = dateModification;
        this.descriptionModification = descriptionModification;
    }

    // Getters et Setters
    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public int getIdPension() {
        return idPension;
    }

    public void setIdPension(int idPension) {
        this.idPension = idPension;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public String getDescriptionModification() {
        return descriptionModification;
    }

    public void setDescriptionModification(String descriptionModification) {
        this.descriptionModification = descriptionModification;
    }
}
