package ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextPane;

import util.AppParams;

public class AProposDialog extends JDialog {
	private final String TITLE = "A propos";
	private final int WIDTH = 320;
	private final int HEIGHT = 240;
	private JTextPane textPane;
	
	public AProposDialog(JFrame parent) {
		this.setTitle(TITLE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(parent);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setResizable(false);
		
		initComponents();
		this.getContentPane().add(textPane, BorderLayout.CENTER);
		this.setVisible(true);
	}

	private void initComponents() {
		textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setBackground(new Color(240, 240, 240));
		textPane.setEditable(false);
		textPane.setSize(WIDTH - 2, HEIGHT - 2);
		
		String content = "<p><strong>Nom du logiciel</strong> : " + AppParams.APP_NAME;
		content += "<br /><strong>Version</strong> : " + AppParams.VERSION;
		content += "<br /><strong>Description</strong> : " + AppParams.DESCRIPTION + "</p>";
		content += "<p><strong>Réalisé par</strong> " + AppParams.AUTHOR;
		content += "<br /><strong>Développé en</strong> " + AppParams.LANGUAGE;
		content += "<br/><strong>Date de sortie</strong> : " + AppParams.RELEASE_DATE + "</p>";
		textPane.setText(content);
	}
}
