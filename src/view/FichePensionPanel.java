package view;

import controller.PensionController;
import modele.Pension;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FichePensionPanel extends JPanel {
    private PensionController pensionController;
    private JTable table;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;

    public FichePensionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.pensionController = new PensionController();
        setLayout(new BorderLayout());

        // Panel titre avec bouton retour
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gestion des Pensions", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton btnRetour = new JButton("Retour");
        btnRetour.addActionListener(e -> mainFrame.showDashboard());
        headerPanel.add(btnRetour, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Création du tableau avec les colonnes nécessaires
        tableModel = new DefaultTableModel(new String[]{"ID", "Adresse", "Responsable", "Téléphone", "Ville"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons CRUD
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Ajouter");
        JButton btnEdit = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");
        JButton btnRefresh = new JButton("Actualiser");
        JButton btnHebergements = new JButton("Gérer les Hébergements");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnHebergements);
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger les pensions existantes
        chargerPensions();

        // Événements
        btnAdd.addActionListener(e -> ajouterPension());
        btnEdit.addActionListener(e -> modifierPension());
        btnDelete.addActionListener(e -> supprimerPension());
        btnRefresh.addActionListener(e -> chargerPensions());
        btnHebergements.addActionListener(e -> gererHebergements());
    }

    private void chargerPensions() {
        tableModel.setRowCount(0);
        try {
            List<Pension> pensions = pensionController.getAllPensions();
            if (pensions != null) {
                for (Pension p : pensions) {
                    tableModel.addRow(new Object[]{
                        p.getId(),
                        p.getAdresse(),
                        p.getResponsable(),
                        p.getTelephone(),
                        p.getVille()
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du chargement des pensions", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des pensions: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterPension() {
        JTextField adresseField = new JTextField();
        JTextField responsableField = new JTextField();
        JTextField telephoneField = new JTextField();
        JTextField villeField = new JTextField();

        Object[] message = {
            "Adresse:", adresseField,
            "Responsable:", responsableField,
            "Téléphone:", telephoneField,
            "Ville:", villeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter une pension", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String adresse = adresseField.getText().trim();
            String responsable = responsableField.getText().trim();
            String telephone = telephoneField.getText().trim();
            String ville = villeField.getText().trim();

            if (!adresse.isEmpty() && !ville.isEmpty()) {
                Pension newPension = new Pension(0, adresse, responsable, telephone, ville);
                try {
                    boolean success = pensionController.ajouterPension(newPension);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Pension ajoutée avec succès !");
                        chargerPensions();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout !", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'ajout: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "L'adresse et la ville sont obligatoires !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierPension() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une pension à modifier !");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        JTextField adresseField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField responsableField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        JTextField telephoneField = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
        JTextField villeField = new JTextField((String) tableModel.getValueAt(selectedRow, 4));

        Object[] message = {
            "Adresse:", adresseField,
            "Responsable:", responsableField,
            "Téléphone:", telephoneField,
            "Ville:", villeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Modifier une pension", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String adresse = adresseField.getText().trim();
            String responsable = responsableField.getText().trim();
            String telephone = telephoneField.getText().trim();
            String ville = villeField.getText().trim();

            if (!adresse.isEmpty() && !ville.isEmpty()) {
                Pension updatedPension = new Pension(id, adresse, responsable, telephone, ville);
                try {
                    boolean success = pensionController.modifierPension(updatedPension);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Pension modifiée avec succès !");
                        chargerPensions();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la modification !", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la modification: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "L'adresse et la ville sont obligatoires !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerPension() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une pension à supprimer !");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, 
            "Voulez-vous vraiment supprimer cette pension ?", 
            "Confirmation de suppression", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                boolean success = pensionController.supprimerPension(id);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Pension supprimée avec succès !");
                    chargerPensions();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression !", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void gererHebergements() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une pension pour gérer ses hébergements !");
            return;
        }

        int pensionId = (int) tableModel.getValueAt(selectedRow, 0);
        mainFrame.showHebergementPanel(pensionId);
    }
}
