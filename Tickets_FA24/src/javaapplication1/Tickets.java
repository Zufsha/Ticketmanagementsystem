//Zufsha Khan
//The Ticket Management System is a simple Java application that helps IIT create, view, and update support tickets. 
//The system allows users to submit ticket details, view all tickets in a list, and make changes when needed
//12/7/24


package javaapplication1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Font;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

    // class level member objects
    Dao dao = new Dao(); // for CRUD operations
    Boolean chkIfAdmin = null;

    // Main menu object items
    private JMenu mnuFile = new JMenu("File");
    private JMenu mnuAdmin = new JMenu("Admin");
    private JMenu mnuTickets = new JMenu("Tickets");

    // Sub menu item objects for all Main menu item objects
    JMenuItem mnuItemExit;
    JMenuItem mnuItemUpdate;
    JMenuItem mnuItemDelete;
    JMenuItem mnuItemOpenTicket;
    JMenuItem mnuItemViewTicket;
    private JTable table;

    public Tickets(Boolean isAdmin) {
    	setBackground(new Color(208, 197, 226));
        chkIfAdmin = isAdmin;
        createMenu();
        prepareGUI();
    }

    private void createMenu() {

        /* Initialize sub menu items **************************************/
        // initialize sub menu item for File main menu
        mnuItemExit = new JMenuItem("Exit");
        mnuItemExit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mnuItemExit.setBackground(new Color(244, 221, 204));
        mnuFile.setFont(new Font("Palatino Linotype", Font.PLAIN, 12));
        mnuFile.add(mnuItemExit);
        
        table = new JTable();
        mnuFile.add(table);

        // initialize first sub menu items for Admin main menu
        mnuItemUpdate = new JMenuItem("Update Ticket");
        mnuItemUpdate.setBackground(new Color(255, 255, 255));
        mnuItemUpdate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mnuAdmin.setFont(new Font("Palatino Linotype", Font.PLAIN, 12));
        mnuAdmin.add(mnuItemUpdate);

        // initialize second sub menu items for Admin main menu
        mnuItemDelete = new JMenuItem("Delete Ticket");
        mnuItemDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        // Disable Delete Ticket option for non-admin users
        if (!chkIfAdmin) {
            mnuItemDelete.setEnabled(false); // Disable delete for non-admins
        }
        mnuAdmin.add(mnuItemDelete);

        // initialize first sub menu item for Tickets main menu
        mnuItemOpenTicket = new JMenuItem("Open Ticket");
        mnuTickets.setFont(new Font("Palatino Linotype", Font.PLAIN, 12));
        mnuTickets.add(mnuItemOpenTicket);

        // initialize second sub menu item for Tickets main menu
        mnuItemViewTicket = new JMenuItem("View Ticket");
        mnuTickets.add(mnuItemViewTicket);

        /* add action listeners for each desired menu item *************/ 
        mnuItemExit.addActionListener(this);
        mnuItemUpdate.addActionListener(this);
        mnuItemDelete.addActionListener(this);
        mnuItemOpenTicket.addActionListener(this);
        mnuItemViewTicket.addActionListener(this);
    }

    private void prepareGUI() {
        // create JMenu bar
        JMenuBar bar = new JMenuBar();
        bar.add(mnuFile); // add main menu items in order, to JMenuBar
        bar.add(mnuAdmin);
        bar.add(mnuTickets);
        // add menu bar components to frame
        setJMenuBar(bar);

        addWindowListener(new WindowAdapter() {
            // define a window close operation
            public void windowClosing(WindowEvent wE) {
                System.exit(0);
            }
        });

        // set frame options
        setSize(400, 400);
        getContentPane().setBackground(new Color(208, 197, 226));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // actions for sub menu items
        if (e.getSource() == mnuItemExit) {
            System.exit(0);
        } else if (e.getSource() == mnuItemOpenTicket) {

            // get ticket information
            String ticketName = JOptionPane.showInputDialog(null, "Enter your name");
            String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");

            // insert the ticket information to database
            int id = dao.insertRecords(ticketName, ticketDesc);

            // display results 
            if (id != 0) {
                System.out.println("Ticket ID : " + id + " created successfully!!!");
                JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
            } else
                System.out.println("Ticket cannot be created!!!");
        } else if (e.getSource() == mnuItemDelete) {
            // only admin can delete tickets
            if (!chkIfAdmin) {
                JOptionPane.showMessageDialog(null, "You do not have permission to delete tickets.");
                return; 
            }

            //geting the ticket ID to delete
            String ticketIdStr = JOptionPane.showInputDialog(null, "Enter Ticket ID to delete");

            try {
                int ticketId = Integer.parseInt(ticketIdStr);
                boolean isDeleted = dao.deleteTicket(ticketId);

                if (isDeleted) {
                    JOptionPane.showMessageDialog(null, "Ticket with ID " + ticketId + " deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Error deleting ticket with ID " + ticketId + ".");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid ticket ID entered.");
            }
        } else if (e.getSource() == mnuItemViewTicket) {
            if (!chkIfAdmin) {
                JOptionPane.showMessageDialog(null, "You do not have permission to view tickets.");
                return;
            }

            // if admin view the ticket
            try {
                JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
                jt.setBounds(30, 40, 200, 400);
                JScrollPane sp = new JScrollPane(jt);
                getContentPane().add(sp);
                setVisible(true); // refreshes or repaints frame on screen
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == mnuItemUpdate) {
            //checking if user is admin
            if (!chkIfAdmin) {
                JOptionPane.showMessageDialog(null, "You do not have permission to update tickets.");
                return; // Exit the action if the user is not an admin
            }

            try {
                
                String ticketIdStr = JOptionPane.showInputDialog(null, "Enter Ticket ID to update:");
                int ticketId = Integer.parseInt(ticketIdStr);
                String newIssuer = JOptionPane.showInputDialog(null, "Enter new ticket issuer:");
                String newDescription = JOptionPane.showInputDialog(null, "Enter new ticket description:");
                
                //update ticket method
                boolean isUpdated = dao.updateTicket(ticketId, newIssuer, newDescription);
                
                // results
                if (isUpdated) {
                    JOptionPane.showMessageDialog(null, "Ticket updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update ticket. Please check the Ticket ID.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid Ticket ID entered.");
            }
        }

    }
}
