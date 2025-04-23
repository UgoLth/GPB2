package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class CatalogueGlobalPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String API_BASE_URL = "http://localhost:8000/api";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private String authToken;

    public CatalogueGlobalPanel(MainFrame mainFrame, String authToken) {
        this.mainFrame = mainFrame;
        this.authToken = authToken;
        
        setLayout(new BorderLayout());

        // Panel titre avec bouton retour
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Catalogue Global des Options", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton btnRetour = new JButton("Retour");
        btnRetour.addActionListener(e -> mainFrame.showFichePensionPanel());
        headerPanel.add(btnRetour, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Création du tableau avec les colonnes nécessaires
        tableModel = new DefaultTableModel(new String[]{"ID", "Option"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons CRUD
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Ajouter une option");
        JButton btnEdit = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");
        JButton btnRefresh = new JButton("Actualiser");
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger les options du catalogue
        chargerCatalogue();

        // Événements
        btnAdd.addActionListener(e -> ajouterOption());
        btnEdit.addActionListener(e -> modifierOption());
        btnDelete.addActionListener(e -> supprimerOption());
        btnRefresh.addActionListener(e -> chargerCatalogue());
    }

    private void chargerCatalogue() {
        tableModel.setRowCount(0);
        try {
            // Récupérer toutes les options du catalogue
            Request request = new Request.Builder()
                .url(API_BASE_URL + "/catalogues")
                .get()
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseBody);
                    
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject catalogueObj = jsonArray.getJSONObject(i);
                        tableModel.addRow(new Object[]{
                            catalogueObj.getInt("id"),
                            catalogueObj.getString("nom")
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors du chargement du catalogue: " + response.code(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement du catalogue: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterOption() {
        JTextField nomField = new JTextField();

        Object[] message = {
            "Nom de l'option:", nomField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter une option au catalogue", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nom = nomField.getText().trim();

            if (!nom.isEmpty()) {
                try {
                    // Créer l'option dans le catalogue
                    JSONObject json = new JSONObject();
                    json.put("nom", nom);
                    
                    RequestBody body = RequestBody.create(json.toString(), JSON);
                    Request request = new Request.Builder()
                        .url(API_BASE_URL + "/catalogues")
                        .post(body)
                        .addHeader("Authorization", "Bearer " + authToken)
                        .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful()) {
                            JOptionPane.showMessageDialog(this, "Option ajoutée avec succès !");
                            chargerCatalogue();
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Erreur lors de la création de l'option: " + response.code(), 
                                "Erreur", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'ajout de l'option: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Le nom de l'option est obligatoire !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void modifierOption() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une option à modifier !");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nomActuel = (String) tableModel.getValueAt(selectedRow, 1);
        
        JTextField nomField = new JTextField(nomActuel);

        Object[] message = {
            "Nom de l'option:", nomField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Modifier une option", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String nouveauNom = nomField.getText().trim();

            if (!nouveauNom.isEmpty()) {
                try {
                    // Modifier l'option dans le catalogue
                    JSONObject json = new JSONObject();
                    json.put("nom", nouveauNom);
                    
                    RequestBody body = RequestBody.create(json.toString(), JSON);
                    Request request = new Request.Builder()
                        .url(API_BASE_URL + "/catalogues/" + id)
                        .put(body)
                        .addHeader("Authorization", "Bearer " + authToken)
                        .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful()) {
                            JOptionPane.showMessageDialog(this, "Option modifiée avec succès !");
                            chargerCatalogue();
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Erreur lors de la modification de l'option: " + response.code(), 
                                "Erreur", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la modification de l'option: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Le nom de l'option est obligatoire !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void supprimerOption() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une option à supprimer !");
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirmation = JOptionPane.showConfirmDialog(this, 
            "Voulez-vous vraiment supprimer l'option \"" + nom + "\" ?\n" +
            "Attention : cela supprimera également tous les tarifs associés à cette option pour toutes les pensions.", 
            "Confirmation de suppression", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                // Supprimer l'option du catalogue
                Request request = new Request.Builder()
                    .url(API_BASE_URL + "/catalogues/" + id)
                    .delete()
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        JOptionPane.showMessageDialog(this, "Option supprimée avec succès !");
                        chargerCatalogue();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur lors de la suppression de l'option: " + response.code(), 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression de l'option: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
