package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class CataloguePensionPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private int pensionId;
    private static final String API_BASE_URL = "http://localhost:8000/api";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private String authToken;

    public CataloguePensionPanel(MainFrame mainFrame, int pensionId, String authToken) {
        this.mainFrame = mainFrame;
        this.pensionId = pensionId;
        this.authToken = authToken;
        
        setLayout(new BorderLayout());

        // Panel titre avec bouton retour
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Catalogue de la Pension", SwingConstants.CENTER);
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
        
        buttonPanel.add(btnAdd);
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger les options du catalogue
        chargerCatalogue();

        // Événements
        btnAdd.addActionListener(e -> ajouterOption());
    }

    private void chargerCatalogue() {
        tableModel.setRowCount(0);
        try {
            // Récupérer les options du catalogue pour cette pension
            Request request = new Request.Builder()
                .url(API_BASE_URL + "/tarif-catalogues?pension_id=" + pensionId)
                .get()
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseBody);
                    
                    // Pour chaque tarif, récupérer l'option correspondante
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject tarifObj = jsonArray.getJSONObject(i);
                        int catalogueId = tarifObj.getInt("catalogue_id");
                        
                        // Récupérer les détails de l'option
                        Request catalogueRequest = new Request.Builder()
                            .url(API_BASE_URL + "/catalogues/" + catalogueId)
                            .get()
                            .addHeader("Authorization", "Bearer " + authToken)
                            .build();
                            
                        try (Response catalogueResponse = client.newCall(catalogueRequest).execute()) {
                            if (catalogueResponse.isSuccessful()) {
                                String catalogueBody = catalogueResponse.body().string();
                                JSONObject catalogueObj = new JSONObject(catalogueBody);
                                
                                tableModel.addRow(new Object[]{
                                    catalogueId,
                                    catalogueObj.getString("nom")
                                });
                            }
                        }
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
                    // 1. Créer l'option dans le catalogue
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
                            String responseBody = response.body().string();
                            JSONObject responseJson = new JSONObject(responseBody);
                            int catalogueId = responseJson.getInt("id");
                            
                            // 2. Associer l'option à la pension avec un prix par défaut de 0
                            JSONObject tarifJson = new JSONObject();
                            tarifJson.put("pension_id", pensionId);
                            tarifJson.put("catalogue_id", catalogueId);
                            tarifJson.put("prix", 0);
                            
                            RequestBody tarifBody = RequestBody.create(tarifJson.toString(), JSON);
                            Request tarifRequest = new Request.Builder()
                                .url(API_BASE_URL + "/tarif-catalogues")
                                .post(tarifBody)
                                .addHeader("Authorization", "Bearer " + authToken)
                                .build();
                                
                            try (Response tarifResponse = client.newCall(tarifRequest).execute()) {
                                if (tarifResponse.isSuccessful()) {
                                    JOptionPane.showMessageDialog(this, "Option ajoutée avec succès !");
                                    chargerCatalogue();
                                } else {
                                    JOptionPane.showMessageDialog(this, 
                                        "Erreur lors de l'association de l'option à la pension: " + tarifResponse.code(), 
                                        "Erreur", 
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            }
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
}
