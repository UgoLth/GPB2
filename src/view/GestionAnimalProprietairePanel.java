package view;

import controller.ProprietaireController;
import modele.Animal;
import modele.Proprietaire;
import service.ApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GestionAnimalProprietairePanel extends JPanel {
    private final ProprietaireController controller;
    private JTable proprietaireTable;
    private JTable animalTable;
    private DefaultTableModel proprietaireModel;
    private DefaultTableModel animalModel;
    private JTextField nomField, prenomField, adresseField, telephoneField, emailField;
    private JPasswordField passwordField;
    private JTextField nomAnimalField, raceField;
    private JComboBox<String> especeComboBox;
    private JTextField dateNaissanceField;
    private Proprietaire selectedProprietaire;
    private Animal selectedAnimal;

    public GestionAnimalProprietairePanel(MainFrame mainFrame) {
        this.controller = new ProprietaireController();
        setLayout(new BorderLayout());

        // Panel principal avec GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Section Propriétaires
        JPanel proprietairePanel = createProprietairePanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(proprietairePanel, gbc);

        // Section Animaux
        JPanel animalPanel = createAnimalPanel();
        gbc.gridy = 1;
        mainPanel.add(animalPanel, gbc);

        // Ajout du panel principal avec scroll
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Bouton retour
        JButton btnRetour = new JButton("Retour au tableau de bord");
        btnRetour.addActionListener(e -> mainFrame.showDashboard());
        add(btnRetour, BorderLayout.SOUTH);

        refreshData();
    }

    private JPanel createProprietairePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gestion des propriétaires"));

        // Table des propriétaires
        String[] columns = {"ID", "Nom", "Prénom", "Email", "Adresse", "Téléphone"};
        proprietaireModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        proprietaireTable = new JTable(proprietaireModel);
        JScrollPane scrollPane = new JScrollPane(proprietaireTable);

        // Formulaire propriétaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Champs du formulaire
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        nomField = new JTextField(20);
        formPanel.add(nomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1;
        prenomField = new JTextField(20);
        formPanel.add(prenomField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Adresse:"), gbc);
        gbc.gridx = 1;
        adresseField = new JTextField(20);
        formPanel.add(adresseField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1;
        telephoneField = new JTextField(20);
        formPanel.add(telephoneField, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Events
        addButton.addActionListener(e -> ajouterProprietaire());
        editButton.addActionListener(e -> modifierProprietaire());
        deleteButton.addActionListener(e -> supprimerProprietaire());

        proprietaireTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = proprietaireTable.getSelectedRow();
                if (row >= 0) {
                    selectedProprietaire = getProprietaireFromRow(row);
                    updateProprietaireFields();
                    refreshAnimalTable(); // Rafraîchir la table des animaux quand on sélectionne un propriétaire
                    clearAnimalFields(); // Vider les champs du formulaire animal
                }
            }
        });

        // Layout
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAnimalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gestion des animaux"));

        // Table des animaux
        String[] animalColumns = {"ID", "Nom", "Race", "Date de naissance", "Espèce"};
        animalModel = new DefaultTableModel(animalColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        animalTable = new JTable(animalModel);
        JScrollPane animalScrollPane = new JScrollPane(animalTable);

        // Formulaire d'ajout/modification d'animal
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Champ Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        nomAnimalField = new JTextField(20);
        formPanel.add(nomAnimalField, gbc);

        // Champ Race
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Race:"), gbc);
        gbc.gridx = 1;
        raceField = new JTextField(20);
        formPanel.add(raceField, gbc);

        // Champ Date de naissance
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date de naissance (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dateNaissanceField = new JTextField(20);
        formPanel.add(dateNaissanceField, gbc);

        // ComboBox Espèce
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Espèce:"), gbc);
        gbc.gridx = 1;
        especeComboBox = new JComboBox<>(new String[]{"Chat", "Chien", "Oiseau", "Rongeur"});
        formPanel.add(especeComboBox, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Ajouter");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Events
        addButton.addActionListener(e -> ajouterAnimal());
        editButton.addActionListener(e -> modifierAnimal());
        deleteButton.addActionListener(e -> supprimerAnimal());

        animalTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = animalTable.getSelectedRow();
                if (row >= 0) {
                    selectedAnimal = getAnimalFromRow(row);
                    updateAnimalFields();
                }
            }
        });

        // Layout
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(animalScrollPane, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshProprietaireTable() {
        proprietaireModel.setRowCount(0);
        try {
            List<Proprietaire> proprietaires = ApiService.getInstance().getProprietaires();
            if (proprietaires != null) {
                for (Proprietaire p : proprietaires) {
                    proprietaireModel.addRow(new Object[]{
                        p.getId(),
                        p.getNom(),
                        p.getPrenom(),
                        p.getEmail(),
                        p.getAdresse(),
                        p.getTelephone()
                    });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la récupération des propriétaires: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAnimalTable() {
        animalModel.setRowCount(0);
        if (selectedProprietaire != null) {
            try {
                List<Animal> animaux = ApiService.getInstance().getAnimaux(selectedProprietaire.getId());
                if (animaux != null) {
                    for (Animal animal : animaux) {
                        animalModel.addRow(new Object[]{
                            animal.getId(),
                            animal.getNom(),
                            animal.getRace(),
                            animal.getDateNaissance() != null ? 
                                new SimpleDateFormat("yyyy-MM-dd").format(animal.getDateNaissance()) : "",
                            animal.getEspeceNom()
                        });
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la récupération des animaux: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ajouterProprietaire() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String adresse = adresseField.getText().trim();
        String telephone = telephoneField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tous les champs sont obligatoires",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Proprietaire p = new Proprietaire();
        p.setNom(nom);
        p.setPrenom(prenom);
        p.setEmail(email);
        p.setPassword(password);
        p.setAdresse(adresse);
        p.setTelephone(telephone);

        if (controller.ajouterProprietaire(p)) {
            clearProprietaireFields();
            refreshProprietaireTable();
        }
    }

    private void modifierProprietaire() {
        if (selectedProprietaire == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un propriétaire à modifier",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String adresse = adresseField.getText().trim();
        String telephone = telephoneField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tous les champs sont obligatoires sauf le mot de passe pour la modification",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedProprietaire.setNom(nom);
        selectedProprietaire.setPrenom(prenom);
        selectedProprietaire.setEmail(email);
        if (!password.isEmpty()) {
            selectedProprietaire.setPassword(password);
        }
        selectedProprietaire.setAdresse(adresse);
        selectedProprietaire.setTelephone(telephone);

        if (controller.modifierProprietaire(selectedProprietaire)) {
            clearProprietaireFields();
            refreshProprietaireTable();
            selectedProprietaire = null;
        }
    }

    private void supprimerProprietaire() {
        if (selectedProprietaire == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un propriétaire à supprimer",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce propriétaire ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.supprimerProprietaire(selectedProprietaire.getId())) {
                clearProprietaireFields();
                refreshProprietaireTable();
                selectedProprietaire = null;
                animalModel.setRowCount(0);
            }
        }
    }

    private void clearProprietaireFields() {
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        passwordField.setText("");
        adresseField.setText("");
        telephoneField.setText("");
    }

    private void updateProprietaireFields() {
        if (selectedProprietaire != null) {
            nomField.setText(selectedProprietaire.getNom());
            prenomField.setText(selectedProprietaire.getPrenom());
            emailField.setText(selectedProprietaire.getEmail());
            passwordField.setText("");
            adresseField.setText(selectedProprietaire.getAdresse());
            telephoneField.setText(selectedProprietaire.getTelephone());
        }
    }

    private Proprietaire getProprietaireFromRow(int row) {
        Proprietaire p = new Proprietaire();
        p.setId((Integer) proprietaireTable.getValueAt(row, 0));
        p.setNom((String) proprietaireTable.getValueAt(row, 1));
        p.setPrenom((String) proprietaireTable.getValueAt(row, 2));
        p.setEmail((String) proprietaireTable.getValueAt(row, 3));
        p.setAdresse((String) proprietaireTable.getValueAt(row, 4));
        p.setTelephone((String) proprietaireTable.getValueAt(row, 5));
        return p;
    }

    private void ajouterAnimal() {
        if (selectedProprietaire == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez d'abord sélectionner un propriétaire",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nom = nomAnimalField.getText().trim();
        String race = raceField.getText().trim();
        String dateStr = dateNaissanceField.getText().trim();
        String especeNom = (String) especeComboBox.getSelectedItem();

        if (nom.isEmpty() || especeNom == null) {
            JOptionPane.showMessageDialog(this,
                "Le nom et l'espèce sont obligatoires",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Animal a = new Animal();
        a.setNom(nom);
        a.setRace(race);
        a.setProprietaireId(selectedProprietaire.getId());
        a.setEspeceId(especeComboBox.getSelectedIndex() + 1); // Temporaire: mapping direct de l'index + 1
        
        if (!dateStr.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                a.setDateNaissance(format.parse(dateStr));
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Utilisez YYYY-MM-DD",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            if (ApiService.getInstance().ajouterAnimal(a)) {
                JOptionPane.showMessageDialog(this,
                    "Animal ajouté avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                clearAnimalFields();
                refreshAnimalTable();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout de l'animal",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ajout de l'animal: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierAnimal() {
        if (selectedAnimal == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un animal à modifier",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nom = nomAnimalField.getText().trim();
        String race = raceField.getText().trim();
        String dateStr = dateNaissanceField.getText().trim();
        String especeNom = (String) especeComboBox.getSelectedItem();

        if (nom.isEmpty() || especeNom == null) {
            JOptionPane.showMessageDialog(this,
                "Le nom et l'espèce sont obligatoires",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedAnimal.setNom(nom);
        selectedAnimal.setRace(race);
        selectedAnimal.setEspeceId(especeComboBox.getSelectedIndex() + 1); // Temporaire: mapping direct de l'index + 1

        if (!dateStr.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                selectedAnimal.setDateNaissance(format.parse(dateStr));
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Utilisez YYYY-MM-DD",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            selectedAnimal.setDateNaissance(null);
        }

        try {
            if (ApiService.getInstance().modifierAnimal(selectedAnimal)) {
                JOptionPane.showMessageDialog(this,
                    "Animal modifié avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                clearAnimalFields();
                refreshAnimalTable();
                selectedAnimal = null;
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification de l'animal",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la modification de l'animal: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerAnimal() {
        if (selectedAnimal == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un animal à supprimer",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer cet animal ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (ApiService.getInstance().supprimerAnimal(selectedAnimal.getId())) {
                    JOptionPane.showMessageDialog(this,
                        "Animal supprimé avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    clearAnimalFields();
                    refreshAnimalTable();
                    selectedAnimal = null;
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression de l'animal",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression de l'animal: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Animal getAnimalFromRow(int row) {
        Animal animal = new Animal();
        animal.setId((Integer) animalTable.getValueAt(row, 0));
        animal.setNom((String) animalTable.getValueAt(row, 1));
        animal.setRace((String) animalTable.getValueAt(row, 2));
        
        String dateStr = (String) animalTable.getValueAt(row, 3);
        if (!dateStr.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                animal.setDateNaissance(format.parse(dateStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        animal.setEspeceNom((String) animalTable.getValueAt(row, 4));
        animal.setProprietaireId(selectedProprietaire.getId());
        animal.setEspeceId(especeComboBox.getSelectedIndex() + 1); // Temporaire: mapping direct
        
        return animal;
    }

    private void updateAnimalFields() {
        if (selectedAnimal != null) {
            nomAnimalField.setText(selectedAnimal.getNom());
            raceField.setText(selectedAnimal.getRace());
            
            if (selectedAnimal.getDateNaissance() != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                dateNaissanceField.setText(format.format(selectedAnimal.getDateNaissance()));
            } else {
                dateNaissanceField.setText("");
            }
            
            // Temporaire: mapping direct de l'especeId vers l'index du combo
            especeComboBox.setSelectedIndex(selectedAnimal.getEspeceId() - 1);
        }
    }

    private void clearAnimalFields() {
        nomAnimalField.setText("");
        raceField.setText("");
        dateNaissanceField.setText("");
        especeComboBox.setSelectedIndex(0);
        selectedAnimal = null;
    }

    // Méthode publique pour rafraîchir les données après la connexion
    public void refreshData() {
        refreshProprietaireTable();
        // Si aucun propriétaire n'est sélectionné, vider la table des animaux
        if (selectedProprietaire == null) {
            animalModel.setRowCount(0);
        } else {
            refreshAnimalTable();
        }
    }
}
