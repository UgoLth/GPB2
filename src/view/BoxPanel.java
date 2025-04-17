package view;

import controller.BoxController;
import modele.Box;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class BoxPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(BoxPanel.class.getName());
    private BoxController boxController;
    private JTable table;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;
    private int tarificationId;
    private int pensionId;

    public BoxPanel(MainFrame mainFrame) {
        this(mainFrame, 0, 0);
    }

    public BoxPanel(MainFrame mainFrame, int tarificationId, int pensionId) {
        this.mainFrame = mainFrame;
        this.tarificationId = tarificationId;
        this.pensionId = pensionId;
        this.boxController = new BoxController();

        setLayout(new BorderLayout());

        // Panel titre avec bouton retour
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gestion des Box", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton btnRetour = new JButton("Retour");
        btnRetour.addActionListener(e -> this.mainFrame.showHebergementPanel(pensionId));
        headerPanel.add(btnRetour, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Création du tableau
        tableModel = new DefaultTableModel(new String[]{"ID", "Numéro", "Type", "Taille", "Disponibilité"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel();
        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnActualiser = new JButton("Actualiser");

        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnActualiser);
        add(buttonPanel, BorderLayout.SOUTH);

        // Événements
        btnAjouter.addActionListener(e -> ajouterBox());
        btnModifier.addActionListener(e -> modifierBox());
        btnSupprimer.addActionListener(e -> supprimerBox());
        btnActualiser.addActionListener(e -> refreshData());

        // Chargement initial des données
        refreshData();
    }

    public void refreshData() {
        LOGGER.info("Rafraîchissement des données des boxes pour tarificationId: " + tarificationId);
        try {
            List<Box> boxes = boxController.getBoxes(tarificationId);
            LOGGER.info("Boxes récupérés: " + (boxes != null ? boxes.size() : "null"));
            
            tableModel.setRowCount(0);
            if (boxes != null) {
                for (Box box : boxes) {
                    tableModel.addRow(new Object[]{
                        box.getId(),
                        box.getNumero(),
                        box.getType(),
                        box.getTaille(),
                        box.getDisponibilite() ? "Disponible" : "Occupé"
                    });
                }
                LOGGER.info("Tableau mis à jour avec " + boxes.size() + " lignes");
            } else {
                LOGGER.warning("Aucun box récupéré");
            }
        } catch (IOException e) {
            LOGGER.severe("Erreur lors du rafraîchissement des données: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des données: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterBox() {
        LOGGER.info("Ouverture du formulaire d'ajout de box");
        JDialog dialog = new JDialog();
        dialog.setTitle("Ajouter un box");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(300, 250);  
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));  
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField numeroField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField tailleField = new JTextField();
        String[] disponibiliteOptions = {"Disponible", "Occupé"};
        JComboBox<String> disponibiliteCombo = new JComboBox<>(disponibiliteOptions);

        formPanel.add(new JLabel("Numéro:"));
        formPanel.add(numeroField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeField);
        formPanel.add(new JLabel("Taille:"));
        formPanel.add(tailleField);
        formPanel.add(new JLabel("Disponibilité:"));
        formPanel.add(disponibiliteCombo);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        saveButton.addActionListener(e -> {
            String numero = numeroField.getText().trim();
            String type = typeField.getText().trim();
            String taille = tailleField.getText().trim();
            boolean disponibilite = disponibiliteCombo.getSelectedItem().equals("Disponible");

            if (numero.isEmpty() || type.isEmpty() || taille.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Tous les champs sont requis", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            LOGGER.info("Tentative d'ajout d'un box : numero=" + numero + 
                       ", type=" + type + ", taille=" + taille + 
                       ", disponibilite=" + disponibilite);
            
            Box newBox = new Box();
            newBox.setNumero(numero);
            newBox.setType(type);
            newBox.setTaille(taille);
            newBox.setDisponibilite(disponibilite);
            newBox.setTarificationId(tarificationId);

            try {
                if (boxController.ajouterBox(newBox, tarificationId)) {
                    LOGGER.info("Box ajouté avec succès");
                    dialog.dispose();
                    refreshData();
                }
            } catch (IOException ex) {
                LOGGER.severe("Erreur lors de l'ajout du box : " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l'ajout du box",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void modifierBox() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un box à modifier", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int boxId = (int) table.getValueAt(selectedRow, 0);
        String currentNumero = (String) table.getValueAt(selectedRow, 1);
        String currentType = (String) table.getValueAt(selectedRow, 2);
        String currentTaille = (String) table.getValueAt(selectedRow, 3);
        boolean currentDisponibilite = table.getValueAt(selectedRow, 4).equals("Disponible");

        JTextField numeroField = new JTextField(currentNumero);
        JTextField typeField = new JTextField(currentType);
        JTextField tailleField = new JTextField(currentTaille);
        String[] disponibiliteOptions = {"Disponible", "Occupé"};
        JComboBox<String> disponibiliteCombo = new JComboBox<>(disponibiliteOptions);
        disponibiliteCombo.setSelectedItem(currentDisponibilite ? "Disponible" : "Occupé");

        Object[] message = {
            "Numéro du box:", numeroField,
            "Type:", typeField,
            "Taille:", tailleField,
            "Disponibilité:", disponibiliteCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Modifier un box",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String numero = numeroField.getText().trim();
                String type = typeField.getText().trim();
                String taille = tailleField.getText().trim();
                boolean disponibilite = disponibiliteCombo.getSelectedItem().equals("Disponible");

                if (numero.isEmpty() || type.isEmpty() || taille.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tous les champs sont requis", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Box updatedBox = new Box(boxId, numero, taille, type, disponibilite, tarificationId);
                boolean success = boxController.modifierBox(updatedBox);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Box modifié avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification du box", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.severe("Erreur lors de la modification du box : " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Erreur lors de la modification : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerBox() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un box à supprimer", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int boxId = (int) table.getValueAt(selectedRow, 0);
        String numero = (String) table.getValueAt(selectedRow, 1);

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer le box " + numero + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                boolean success = boxController.supprimerBox(boxId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Box supprimé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du box", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                LOGGER.severe("Erreur lors de la suppression du box : " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
