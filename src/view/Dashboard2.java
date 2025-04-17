package view;

import javax.swing.*;
import java.awt.*;

public class Dashboard2 extends JPanel {

    private static final long serialVersionUID = 1L;
    private MainFrame mainFrame;
    private JLabel lblUser;

    public Dashboard2(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null);

        JLabel lblTitle = new JLabel("Tableau de bord");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 25));
        lblTitle.setBounds(356, 29, 226, 38);
        add(lblTitle);

        lblUser = new JLabel("Connecté en tant que : " + getUserName());
        lblUser.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblUser.setBounds(30, 29, 400, 30);
        add(lblUser);

        // Gestion des Animaux et Propriétaires
        JButton btnGestionAnimauxProprietaires = new JButton("Gestion des Animaux et Propriétaires");
        btnGestionAnimauxProprietaires.setFont(new Font("Tahoma", Font.BOLD, 20));
        btnGestionAnimauxProprietaires.setBounds(328, 127, 319, 73);
        add(btnGestionAnimauxProprietaires);
        btnGestionAnimauxProprietaires.addActionListener(e -> mainFrame.showGestionAnimalProprietairePanel());

        JButton btnFichePension = new JButton("Fiche Pension");
        btnFichePension.setFont(new Font("Tahoma", Font.BOLD, 20));
        btnFichePension.setBounds(328, 231, 319, 73);
        add(btnFichePension);
        btnFichePension.addActionListener(e -> mainFrame.showFichePensionPanel());

        // Rediriger vers la gestion des pensions pour gérer les hébergements
        JButton btnGestionDesHebergements = new JButton("Gestion des hébergements");
        btnGestionDesHebergements.setFont(new Font("Tahoma", Font.BOLD, 20));
        btnGestionDesHebergements.setBounds(328, 332, 319, 73);
        add(btnGestionDesHebergements);
        btnGestionDesHebergements.addActionListener(e -> {
            mainFrame.showFichePensionPanel();
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez une pension pour gérer ses hébergements", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        // Rediriger vers la gestion des pensions pour gérer les box
        JButton btnGestionDesBox = new JButton("Gestion des Box");
        btnGestionDesBox.setFont(new Font("Tahoma", Font.BOLD, 20));
        btnGestionDesBox.setBounds(328, 440, 319, 73);
        add(btnGestionDesBox);
        btnGestionDesBox.addActionListener(e -> {
            mainFrame.showFichePensionPanel();
            JOptionPane.showMessageDialog(this, 
                "Sélectionnez une pension puis un hébergement pour gérer ses box", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnJournalDesModifications = new JButton("Journal des modifications");
        btnJournalDesModifications.setFont(new Font("Tahoma", Font.BOLD, 20));
        btnJournalDesModifications.setBounds(328, 550, 319, 73);
        add(btnJournalDesModifications);
        btnJournalDesModifications.addActionListener(e -> mainFrame.showJournalDesModificationsPanel());

        JButton btnLogout = new JButton("Déconnexion");
        btnLogout.setFont(new Font("Tahoma", Font.BOLD, 18));
        btnLogout.setBounds(30, 550, 200, 50);
        add(btnLogout);
        btnLogout.addActionListener(e -> mainFrame.logout());
    }

    private String getUserName() {
        String token = mainFrame.getAuthToken();
        if (token != null && !token.isEmpty()) {
            return "Utilisateur connecté";
        }
        return "Utilisateur inconnu";
    }
}
