//Zufsha Khan
//The Ticket Management System is a simple Java application that helps IIT create, view, and update support tickets. 
//The system allows users to submit ticket details, view all tickets in a list, and make changes when needed
//12/7/24


package javaapplication1;

import java.awt.GridLayout; //useful for layouts
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//controls-label text fields, button
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;

@SuppressWarnings("serial")
public class Login extends JFrame {

	Dao conn;

	public Login() {

		super("IIT HELP DESK LOGIN");
		getContentPane().setBackground(new Color(216, 148, 150));
		setForeground(new Color(255, 130, 133));
		conn = new Dao();
		conn.createTables();
		setSize(491, 284);
		getContentPane().setLayout(new GridLayout(4, 2));
		setLocationRelativeTo(null); // centers window

		// colors for the gui
		JLabel lblUsername = new JLabel("Username", JLabel.LEFT);
		lblUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblUsername.setBackground(new Color(216, 148, 150));
		JLabel lblPassword = new JLabel("Password", JLabel.LEFT);
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPassword.setBackground(new Color(216, 148, 150));
		JLabel lblStatus = new JLabel(" ", JLabel.CENTER);
		
		// SET UP CONTROLS
		JTextField txtUname = new JTextField(10);
		txtUname.setBackground(new Color(255, 242, 243));

		JPasswordField txtPassword = new JPasswordField();
		txtPassword.setBackground(new Color(255, 242, 243));
		JButton btnExit = new JButton("Exit");
		btnExit.setForeground(new Color(0, 0, 0));
		btnExit.setBackground(new Color(208, 197, 226));
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 11));

		// constraints

		lblStatus.setToolTipText("Contact help desk to unlock password");
		lblUsername.setHorizontalAlignment(JLabel.CENTER);
		lblPassword.setHorizontalAlignment(JLabel.CENTER);
 
		// ADD OBJECTS TO FRAME
		getContentPane().add(lblUsername);  // 1st row filler
		getContentPane().add(txtUname);
		getContentPane().add(lblPassword);  // 2nd row
		getContentPane().add(txtPassword);
		getContentPane().add(btnExit);
		getContentPane().add(lblStatus);    // 4th row
		JButton btn = new JButton("Submit");
		btn.setBackground(new Color(208, 197, 226));
		btn.setFont(new Font("Tahoma", Font.BOLD, 11));
		getContentPane().add(btn);          // 3rd row

		btn.addActionListener(new ActionListener() {
			int count = 0; // count agent

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean admin = false;
				count = count + 1;
				// verify credentials of user (MAKE SURE TO CHANGE TO YOUR TABLE NAME BELOW)

				String query = "SELECT * FROM zkhan_users WHERE uname = ? and upass = ?;";
				try (PreparedStatement stmt = conn.getConnection().prepareStatement(query)) {
					stmt.setString(1, txtUname.getText());
					stmt.setString(2, txtPassword.getText());
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						admin = rs.getBoolean("admin"); // get table column value
						new Tickets(admin); //open Tickets file / GUI interface
						setVisible(false); // HIDE THE FRAME
						dispose(); // CLOSE OUT THE WINDOW
					} else
						lblStatus.setText("Try again! " + (3 - count) + " / 3 attempt(s) left");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
 			 
			}
		});
		btnExit.addActionListener(e -> System.exit(0));

		setVisible(true); // SHOW THE FRAME
	}

	public static void main(String[] args) {

		new Login();
	}
	
}

