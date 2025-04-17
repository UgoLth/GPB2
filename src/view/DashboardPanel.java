package view;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public DashboardPanel(MainFrame mainFrame) {
        setLayout(null); 

        JLabel lblTitle = new JLabel("Tableau de bord");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 25));
        lblTitle.setBounds(356, 29, 226, 38);
        add(lblTitle);

        JButton btnFichePension = new JButton("Fiche Pension");
        btnFichePension.setFont(new Font("Tahoma", Font.BOLD, 20));
        btnFichePension.setBounds(328, 127, 319, 73);
        add(btnFichePension);
        btnFichePension.addActionListener(e -> mainFrame.showFichePensionPanel());

        JButton btnGestionDesHbergements = new JButton("Gestion des hébergements");
        btnGestionDesHbergements.setFont(new Font("Tahoma", Font.BOLD, 20));
        btnGestionDesHbergements.setBounds(328, 231, 319, 73);
        add(btnGestionDesHbergements);

        JButton btnGestionDesBox = new JButton("Gestion des Box");
        btnGestionDesBox.setFont(new Font("Tahoma", Font.BOLD, 20));
        btnGestionDesBox.setBounds(328, 332, 319, 73);
        add(btnGestionDesBox);

        JButton btnJournalDesModifications = new JButton("Journal des modifications");
        btnJournalDesModifications.setFont(new Font("Tahoma", Font.BOLD, 20));
        btnJournalDesModifications.setBounds(328, 440, 319, 73);
        add(btnJournalDesModifications);

        JButton btnGestionProprietaires = new JButton("Gestion des Propriétaires");
        btnGestionProprietaires.setFont(new Font("Tahoma", Font.BOLD, 20));
        btnGestionProprietaires.setBounds(328, 550, 319, 73); 
        add(btnGestionProprietaires);

        btnGestionProprietaires.addActionListener(e -> mainFrame.showGestionAnimalProprietairePanel());
    }
}
