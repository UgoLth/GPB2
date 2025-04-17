package view;

import javax.swing.*;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel dashboardPanel;
    private JPanel fichePensionPanel;
    private JPanel gestionAnimalProprietairePanel;
    private BoxPanel boxPanel;
    private String authToken;
    private static final String LOGIN_URL = "http://localhost:8000/api/login";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private ProprietairePanel proprietairePanel;
    private HebergementPanel hebergementPanel;
    private int currentPensionId;

    public MainFrame() {
        setTitle("Application de Gestion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new CardLayout());
        loginPanel = createLoginPanel();
        dashboardPanel = new Dashboard2(this);
        fichePensionPanel = new FichePensionPanel(this);
        gestionAnimalProprietairePanel = new GestionAnimalProprietairePanel(this);

        showLoginPanel();
    }

    public void showDashboard() {
        mainPanel.removeAll();
        mainPanel.add(dashboardPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showLoginPanel() {
        mainPanel.removeAll();
        mainPanel.add(loginPanel);
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Mot de passe:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Se connecter");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            verifierIdentifiants(email, password);
        });

        return panel;
    }

    private void verifierIdentifiants(String email, String password) {
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
            .url(LOGIN_URL)
            .post(body)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(MainFrame.this, 
                        "Erreur de connexion: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if (jsonResponse.has("token")) {
                        authToken = jsonResponse.getString("token"); 
                        service.ApiService.setAuthToken(authToken);
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(MainFrame.this, "Connexion réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                            showDashboard();
                            // Rafraîchir les données du panel de gestion après la connexion
                            if (gestionAnimalProprietairePanel != null) {
                                ((GestionAnimalProprietairePanel) gestionAnimalProprietairePanel).refreshData();
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(MainFrame.this, 
                                "Réponse invalide du serveur", 
                                "Erreur", 
                                JOptionPane.ERROR_MESSAGE));
                    }
                } else {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(MainFrame.this, 
                            "Identifiants invalides", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE));
                }
            }
        });
    }

    public void showProprietairePanel() {
        if (proprietairePanel == null) {
            proprietairePanel = new ProprietairePanel(this);
        }
        mainPanel.removeAll();
        mainPanel.add(proprietairePanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showBoxPanel() {
        if (boxPanel == null) {
            boxPanel = new BoxPanel(this);
        }
        mainPanel.removeAll();
        mainPanel.add(boxPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showBoxPanel(int tarificationId) {
        boxPanel = new BoxPanel(this, tarificationId, currentPensionId);
        mainPanel.removeAll();
        mainPanel.add(boxPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showFichePensionPanel() {
        mainPanel.removeAll();
        mainPanel.add(fichePensionPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showGestionAnimalProprietairePanel() {
        mainPanel.removeAll();
        mainPanel.add(gestionAnimalProprietairePanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showHebergementPanel(int pensionId) {
        this.currentPensionId = pensionId;
        if (hebergementPanel == null) {
            hebergementPanel = new HebergementPanel(this, pensionId);
        } else {
            hebergementPanel.refreshData(pensionId);
        }
        mainPanel.removeAll();
        mainPanel.add(hebergementPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showPensionPanel() {
        showFichePensionPanel();
    }

    public void setUserToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void logout() {
        this.authToken = null;
        JOptionPane.showMessageDialog(this, "Déconnexion réussie !");
        showLoginPanel();
    }

    public void showJournalDesModificationsPanel() {
        JOptionPane.showMessageDialog(this, "Le journal des modifications n'est pas encore implémenté.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
