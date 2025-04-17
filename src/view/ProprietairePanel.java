package view;

import controller.ProprietaireController;
import modele.Proprietaire;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProprietairePanel extends JPanel {
    private ProprietaireController proprietaireController;
    private JTable proprietaireTable;
    private DefaultTableModel tableModel;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField telephoneField;
    private JTextField adresseField;
    private final MainFrame mainFrame;

    public ProprietairePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.proprietaireController = new ProprietaireController();
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Panel d'en-tête avec titre et bouton retour
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Gestion des Propriétaires");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton btnRetour = new JButton("Retour");
        btnRetour.addActionListener(e -> mainFrame.showDashboard());
        headerPanel.add(btnRetour, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        nomField = new JTextField();
        prenomField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();
        telephoneField = new JTextField();
        adresseField = new JTextField();

        formPanel.add(new JLabel("Nom:"));
        formPanel.add(nomField);
        formPanel.add(new JLabel("Prénom:"));
        formPanel.add(prenomField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Mot de passe:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Téléphone:"));
        formPanel.add(telephoneField);
        formPanel.add(new JLabel("Adresse:"));
        formPanel.add(adresseField);

        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.add(formPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Ajouter");
        JButton modifyButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");

        addButton.addActionListener(e -> handleAdd());
        modifyButton.addActionListener(e -> handleModify());
        deleteButton.addActionListener(e -> handleDelete());

        buttonPanel.add(addButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);

        formContainer.add(buttonPanel, BorderLayout.SOUTH);
        add(formContainer, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Adresse"};
        tableModel = new DefaultTableModel(columnNames, 0);
        proprietaireTable = new JTable(tableModel);
        proprietaireTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && proprietaireTable.getSelectedRow() != -1) {
                int row = proprietaireTable.getSelectedRow();
                nomField.setText((String) tableModel.getValueAt(row, 1));
                prenomField.setText((String) tableModel.getValueAt(row, 2));
                emailField.setText((String) tableModel.getValueAt(row, 3));
                passwordField.setText("");
                telephoneField.setText((String) tableModel.getValueAt(row, 4));
                adresseField.setText((String) tableModel.getValueAt(row, 5));
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(proprietaireTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void handleAdd() {
        if (validateFields(true)) {
            Proprietaire newProprietaire = new Proprietaire(
                nomField.getText(),
                prenomField.getText(),
                adresseField.getText(),
                telephoneField.getText(),
                emailField.getText(),
                new String(passwordField.getPassword())
            );

            if (proprietaireController.ajouterProprietaire(newProprietaire)) {
                clearFields();
                refreshData();
                JOptionPane.showMessageDialog(this,
                    "Propriétaire ajouté avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout du propriétaire",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleModify() {
        int selectedRow = proprietaireTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un propriétaire à modifier",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (validateFields(false)) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            Proprietaire updatedProprietaire = new Proprietaire(
                nomField.getText(),
                prenomField.getText(),
                adresseField.getText(),
                telephoneField.getText(),
                emailField.getText(),
                passwordField.getPassword().length > 0 ? new String(passwordField.getPassword()) : null
            );
            updatedProprietaire.setId(id);

            if (proprietaireController.modifierProprietaire(updatedProprietaire)) {
                clearFields();
                refreshData();
                JOptionPane.showMessageDialog(this,
                    "Propriétaire modifié avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification du propriétaire",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDelete() {
        int selectedRow = proprietaireTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un propriétaire à supprimer",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce propriétaire ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            if (proprietaireController.supprimerProprietaire(id)) {
                clearFields();
                refreshData();
                JOptionPane.showMessageDialog(this,
                    "Propriétaire supprimé avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression du propriétaire",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateFields(boolean isNewProprietaire) {
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || 
            emailField.getText().isEmpty() || telephoneField.getText().isEmpty() || 
            adresseField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tous les champs sont obligatoires" + (isNewProprietaire ? ", y compris le mot de passe" : " sauf le mot de passe"),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (isNewProprietaire && passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this,
                "Le mot de passe est obligatoire pour un nouveau propriétaire",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearFields() {
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        passwordField.setText("");
        telephoneField.setText("");
        adresseField.setText("");
        proprietaireTable.clearSelection();
    }

    private void refreshData() {
        List<Proprietaire> proprietaires = proprietaireController.getProprietaires();
        tableModel.setRowCount(0);
        if (proprietaires != null) {
            for (Proprietaire p : proprietaires) {
                Object[] row = {
                    p.getId(),
                    p.getNom(),
                    p.getPrenom(),
                    p.getEmail(),
                    p.getTelephone(),
                    p.getAdresse()
                };
                tableModel.addRow(row);
            }
        }
    }
}
