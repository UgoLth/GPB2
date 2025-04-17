package view;

import modele.Tarif;
import service.TarifDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TarifFrame extends JFrame {
    private JTable tableTarifs;
    private DefaultTableModel tableModel;
    private JTextField txtPensionId, txtTypeGardiennageId, txtTarif;
    private JButton btnModifier, btnAjouter, btnSupprimer, btnRetour;
    private TarifDAO tarifDAO;

    public TarifFrame(MainFrame mainFrame) {
        setTitle("Gestion des Hébergements");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        tarifDAO = new TarifDAO();

        JLabel lblTarifs = new JLabel("Gestion des Hébergements");
        lblTarifs.setFont(new Font("Tahoma", Font.BOLD, 25));
        lblTarifs.setBounds(20, 10, 400, 30);
        add(lblTarifs);

        tableModel = new DefaultTableModel(new String[]{"Pension ID", "Type Gardiennage ID", "Tarif"}, 0);
        tableTarifs = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableTarifs);
        scrollPane.setBounds(20, 50, 550, 150);
        add(scrollPane);

        JLabel lblPensionId = new JLabel("Pension ID :");
        lblPensionId.setBounds(20, 220, 100, 30);
        add(lblPensionId);

        txtPensionId = new JTextField();
        txtPensionId.setBounds(120, 220, 100, 30);
        add(txtPensionId);

        JLabel lblTypeGardiennageId = new JLabel("Type Gardiennage ID :");
        lblTypeGardiennageId.setBounds(240, 220, 150, 30);
        add(lblTypeGardiennageId);

        txtTypeGardiennageId = new JTextField();
        txtTypeGardiennageId.setBounds(400, 220, 100, 30);
        add(txtTypeGardiennageId);

        JLabel lblTarif = new JLabel("Tarif :");
        lblTarif.setBounds(20, 260, 100, 30);
        add(lblTarif);

        txtTarif = new JTextField();
        txtTarif.setBounds(120, 260, 100, 30);
        add(txtTarif);


        btnModifier = new JButton("Modifier");
        btnModifier.setBounds(20, 300, 100, 30);
        add(btnModifier);

        btnAjouter = new JButton("Ajouter");
        btnAjouter.setBounds(140, 300, 100, 30);
        add(btnAjouter);

        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setBounds(260, 300, 100, 30);
        add(btnSupprimer);

        btnRetour = new JButton("Retour");
        btnRetour.setBounds(380, 300, 100, 30);
        add(btnRetour);

        btnModifier.addActionListener(e -> modifierTarif());
        btnAjouter.addActionListener(e -> ajouterTarif());
        btnSupprimer.addActionListener(e -> supprimerTarif());
        btnRetour.addActionListener(e -> {
            mainFrame.showDashboard(); 
        });


        loadTarifs();
    }

    private void loadTarifs() {
        tableModel.setRowCount(0);
        List<Tarif> tarifs = tarifDAO.getAllTarifs();
        for (Tarif tarif : tarifs) {
            tableModel.addRow(new Object[]{tarif.getPensionId(), tarif.getTypeGardiennageId(), tarif.getTarif()});
        }
    }

    private void modifierTarif() {
        try {
            int pensionId = Integer.parseInt(txtPensionId.getText());
            int typeGardiennageId = Integer.parseInt(txtTypeGardiennageId.getText());
            double tarif = Double.parseDouble(txtTarif.getText());

            tarifDAO.updateTarif(pensionId, typeGardiennageId, tarif);
            JOptionPane.showMessageDialog(this, "Tarif modifié avec succès !");
            loadTarifs();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs valides.");
        }
    }

    private void ajouterTarif() {
        try {
            int pensionId = Integer.parseInt(txtPensionId.getText());
            int typeGardiennageId = Integer.parseInt(txtTypeGardiennageId.getText());
            double tarif = Double.parseDouble(txtTarif.getText());

            tarifDAO.insertTarif(pensionId, typeGardiennageId, tarif);
            JOptionPane.showMessageDialog(this, "Tarif ajouté avec succès !");
            loadTarifs();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs valides.");
        }
    }

    private void supprimerTarif() {
        try {
            int selectedRow = tableTarifs.getSelectedRow();
            if (selectedRow >= 0) {
                int pensionId = (int) tableModel.getValueAt(selectedRow, 0);
                int typeGardiennageId = (int) tableModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(this, "Confirmez la suppression ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tarifDAO.deleteTarif(pensionId, typeGardiennageId);
                    JOptionPane.showMessageDialog(this, "Tarif supprimé avec succès !");
                    loadTarifs();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un tarif à supprimer.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression.");
        }
    }
}
