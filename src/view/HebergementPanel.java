package view;

import controller.HebergementController;
import modele.Tarification;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class HebergementPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(HebergementPanel.class.getName());
    private HebergementController hebergementController;
    private JTable table;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;
    private int currentPensionId;
    private static final Map<Integer, String> TYPE_GARDIENNAGE_MAP = Map.of(
        1, "Hôtel Canin",
        2, "Camping Canin",
        3, "Pension Féline"
    );

    public HebergementPanel(MainFrame mainFrame, int pensionId) {
        LOGGER.info("Création d'un nouveau panneau d'hébergement pour la pension " + pensionId);
        this.mainFrame = mainFrame;
        this.currentPensionId = pensionId;
        this.hebergementController = new HebergementController();
        setLayout(new BorderLayout());

        // Panel titre avec bouton retour
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gestion des Hébergements", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton btnRetour = new JButton("Retour");
        btnRetour.addActionListener(e -> this.mainFrame.showPensionPanel());
        headerPanel.add(btnRetour, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Création du tableau avec les colonnes nécessaires
        tableModel = new DefaultTableModel(new String[]{"ID", "Type de Gardiennage", "Tarif"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        
        // Ajouter un gestionnaire de double-clic sur les lignes
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int tarificationId = (int) tableModel.getValueAt(row, 0);
                        LOGGER.info("Double-clic sur la tarification " + tarificationId);
                        mainFrame.showBoxPanel(tarificationId);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons CRUD
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Ajouter");
        JButton btnEdit = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");
        JButton btnRefresh = new JButton("Actualiser");
        JButton btnBox = new JButton("Gérer les Box");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBox);
        add(buttonPanel, BorderLayout.SOUTH);

        // Événements
        btnAdd.addActionListener(e -> ajouterTarification());
        btnEdit.addActionListener(e -> modifierTarification());
        btnDelete.addActionListener(e -> supprimerTarification());
        btnRefresh.addActionListener(e -> refreshData(currentPensionId));
        btnBox.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int tarificationId = (int) tableModel.getValueAt(selectedRow, 0);
                LOGGER.info("Ouverture des box pour la tarification " + tarificationId);
                mainFrame.showBoxPanel(tarificationId);
            } else {
                LOGGER.warning("Aucune tarification sélectionnée");
                JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une tarification",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Charger les données initiales
        refreshData(pensionId);
    }

    public void refreshData(int pensionId) {
        LOGGER.info("Rafraîchissement des données pour la pension " + pensionId);
        this.currentPensionId = pensionId;
        tableModel.setRowCount(0);
        List<Tarification> tarifications = hebergementController.getTarifications(pensionId);
        
        if (tarifications == null) {
            LOGGER.warning("Échec de la récupération des tarifications");
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la récupération des tarifs. Veuillez vérifier la connexion au serveur.",
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        LOGGER.info("Nombre de tarifications récupérées : " + tarifications.size());
        for (Tarification t : tarifications) {
            String typeGardiennage = TYPE_GARDIENNAGE_MAP.get(t.getTypeGardiennageId());
            if (typeGardiennage == null) {
                LOGGER.warning("Type de gardiennage inconnu : " + t.getTypeGardiennageId());
                typeGardiennage = "Type inconnu";
            }
            
            tableModel.addRow(new Object[]{
                t.getId(),
                typeGardiennage,
                String.format("%.2f €", t.getTarif())
            });
        }
        LOGGER.info("Tableau mis à jour avec " + tableModel.getRowCount() + " lignes");
        JOptionPane.showMessageDialog(this, "Données actualisées avec succès !", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void ajouterTarification() {
        LOGGER.info("Tentative d'ajout d'une tarification");
        String[] types = TYPE_GARDIENNAGE_MAP.values().toArray(new String[0]);
        JComboBox<String> typeCombo = new JComboBox<>(types);
        JTextField tarifField = new JTextField();

        Object[] message = {
            "Type de gardiennage:", typeCombo,
            "Tarif:", tarifField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter un tarif", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String typeStr = (String) typeCombo.getSelectedItem();
            String tarifText = tarifField.getText().trim();

            if (!tarifText.isEmpty()) {
                try {
                    double tarif = Double.parseDouble(tarifText);
                    int typeGardiennageId = getTypeGardiennageId(typeStr);
                    
                    if (typeGardiennageId == -1) {
                        LOGGER.warning("Type de gardiennage non trouvé : " + typeStr);
                        JOptionPane.showMessageDialog(this, 
                            "Erreur : type de gardiennage non trouvé. Veuillez vérifier la liste des types de gardiennage.",
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    LOGGER.info("Tentative d'ajout d'une tarification : type=" + typeGardiennageId + ", tarif=" + tarif + ", pension=" + currentPensionId);
                    Tarification newTarification = new Tarification(0, typeGardiennageId, tarif, currentPensionId);
                    
                    boolean success = hebergementController.ajouterTarification(newTarification);
                    if (success) {
                        LOGGER.info("Tarification ajoutée avec succès");
                        JOptionPane.showMessageDialog(this, "Tarif ajouté avec succès !");
                        refreshData(currentPensionId);
                    } else {
                        LOGGER.warning("Échec de l'ajout de la tarification");
                        JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout ! Veuillez vérifier les données saisies.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.warning("Format de tarif invalide : " + tarifText);
                    JOptionPane.showMessageDialog(this, "Le tarif doit être un nombre valide !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                LOGGER.warning("Tentative d'ajout avec un tarif vide");
                JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierTarification() {
        LOGGER.info("Tentative de modification d'une tarification");
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            LOGGER.warning("Tentative de modification sans sélection");
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un tarif à modifier !");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String[] types = TYPE_GARDIENNAGE_MAP.values().toArray(new String[0]);
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 1));
        
        // Enlever le symbole € et convertir en nombre
        String currentTarif = tableModel.getValueAt(selectedRow, 2).toString();
        currentTarif = currentTarif.replace(" €", "");
        JTextField tarifField = new JTextField(currentTarif);

        Object[] message = {
            "Type de gardiennage:", typeCombo,
            "Tarif:", tarifField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Modifier un tarif", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String typeStr = (String) typeCombo.getSelectedItem();
            String tarifText = tarifField.getText().trim();

            if (!tarifText.isEmpty()) {
                try {
                    double tarif = Double.parseDouble(tarifText);
                    int typeGardiennageId = getTypeGardiennageId(typeStr);
                    
                    if (typeGardiennageId == -1) {
                        LOGGER.warning("Type de gardiennage non trouvé : " + typeStr);
                        JOptionPane.showMessageDialog(this, 
                            "Erreur : type de gardiennage non trouvé. Veuillez vérifier la liste des types de gardiennage.",
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    LOGGER.info("Tentative de modification d'une tarification : id=" + id + ", type=" + typeGardiennageId + ", tarif=" + tarif);
                    Tarification updatedTarification = new Tarification(id, typeGardiennageId, tarif, currentPensionId);
                    
                    boolean success = hebergementController.modifierTarification(updatedTarification);
                    if (success) {
                        LOGGER.info("Tarification modifiée avec succès");
                        JOptionPane.showMessageDialog(this, "Tarif modifié avec succès !");
                        refreshData(currentPensionId);
                    } else {
                        LOGGER.warning("Échec de la modification de la tarification");
                        JOptionPane.showMessageDialog(this, "Erreur lors de la modification ! Veuillez vérifier les données saisies.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.warning("Format de tarif invalide : " + tarifText);
                    JOptionPane.showMessageDialog(this, "Le tarif doit être un nombre valide !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                LOGGER.warning("Tentative de modification avec un tarif vide");
                JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerTarification() {
        LOGGER.info("Tentative de suppression d'une tarification");
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            LOGGER.warning("Tentative de suppression sans sélection");
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un tarif à supprimer !");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int option = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce tarif ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            LOGGER.info("Tentative de suppression de la tarification " + id);
            boolean success = hebergementController.supprimerTarification(id);
            if (success) {
                LOGGER.info("Tarification supprimée avec succès");
                JOptionPane.showMessageDialog(this, "Tarif supprimé avec succès !");
                refreshData(currentPensionId);
            } else {
                LOGGER.warning("Échec de la suppression de la tarification");
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression ! Veuillez vérifier les données.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getTypeGardiennageId(String libelle) {
        for (Map.Entry<Integer, String> entry : TYPE_GARDIENNAGE_MAP.entrySet()) {
            if (entry.getValue().equals(libelle)) {
                return entry.getKey();
            }
        }
        LOGGER.warning("Type de gardiennage non trouvé : " + libelle);
        return -1;
    }
}
