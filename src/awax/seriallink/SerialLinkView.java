/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Julien Le Sauce
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package awax.seriallink;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import awax.seriallink.seriallink.FlowControl;
import awax.seriallink.seriallink.Parity;
import awax.seriallink.seriallink.SerialLinkParameters;
import net.miginfocom.swing.MigLayout;

/**
 * Fenêtre de gestion de la liaison série.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class SerialLinkView extends JFrame implements ActionListener {

	private static final long serialVersionUID = 735327939995591101L;

	private final SerialLinkModel model;
	private final SerialLinkController controller;
	private final Logger logger;

	private JLabel lblPortName;
	private JLabel lblBaudRate;
	private JLabel lblFlowControlIn;
	private JLabel lblFlowControlOut;
	private JLabel lblDataBits;
	private JLabel lblStopBits;
	private JLabel lblParity;
	private JLabel lblRecvTimeout;
	private JLabel lblState;
	private JLabel lblLedConnected;
	private JLabel lblActivity;
	private JLabel lblIncomingMsgLED;

	private JComboBox<String> boxBaudRate;
	private JComboBox<FlowControl> boxFlowControlIn;
	private JComboBox<FlowControl> boxFlowControlOut;
	private JComboBox<String> boxDataBits;
	private JComboBox<String> boxStopBits;
	private JComboBox<Parity> boxParity;
	private JSpinner spRecvTimeout;

	private JButton btnOpenPort;
	private JButton btnClosePort;

	private JTextField tfPortName;
	private JTextPane console;

	/**
	 * Permet d'instancier la fenêtre de gestion de la liaison série.
	 * 
	 * @param model
	 *            Modèle de données de la fenêtre.
	 * @param controller
	 *            Contrôleur de la fenêtre.
	 */
	public SerialLinkView (final SerialLinkModel model, final SerialLinkController controller) {
		super("Serial Link");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.model = model;
		this.controller = controller;
		this.logger = Logger.getLogger(SerialLinkView.class);
		createView();
		setStyle();
		setOnListeners();
	}

	/**
	 * Permet d'afficher l'interface graphique de l'application.
	 */
	public void showGui () {
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Permet d'afficher si la liaison série est connecté ou non.
	 * 
	 * @param isConnected
	 *            <code>true</code> si l'on est connecté, <code>false</code>s
	 *            sinon.
	 */
	public synchronized void setConnected (boolean isConnected) {
		this.lblLedConnected.setIcon(isConnected ? SerialLinkModel.LED_GREEN : SerialLinkModel.LED_RED);
	}

	/**
	 * Permet de mettre à jour l'interface graphique avec les paramètres
	 * précisés.
	 * 
	 * @param params
	 *            Nouveaux paramètres à prendre en compte.
	 */
	public void updateParameters (final SerialLinkParameters params) {
		this.tfPortName.setText(params.getComId());
		this.boxBaudRate.setSelectedItem(params.getBaudRate());
		this.boxFlowControlIn.setSelectedItem(SerialLinkParameters.getFlowControl(params.getFlowControlIn()));
		this.boxFlowControlOut.setSelectedItem(SerialLinkParameters.getFlowControl(params.getFlowControlOut()));
		this.boxDataBits.setSelectedItem(params.getDatabits());
		this.boxStopBits.setSelectedItem(params.getStopbits());
		this.boxParity.setSelectedItem(params.getParity());
		this.spRecvTimeout.setValue(params.getRecvTimeout());
	}

	/**
	 * Permet d'afficher du texte dans la console.
	 * 
	 * @param text
	 *            Texte à afficher dans la console.
	 * @param textColor
	 *            Couleur du texte à afficher.
	 * @param fontStyle
	 *            Style du texte à afficher.
	 */
	public synchronized void appendConsole (String text, Color textColor, int fontStyle) {
		StyledDocument doc = this.console.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();

		Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
		Style mainStyle = sc.addStyle("MainStyle", defaultStyle);
		StyleConstants.setForeground(mainStyle, textColor);
		StyleConstants.setFontSize(mainStyle, 13);

		if (fontStyle == Font.BOLD) {
			StyleConstants.setBold(mainStyle, true);
			StyleConstants.setItalic(mainStyle, false);
		} else if (fontStyle == Font.ITALIC) {
			StyleConstants.setItalic(mainStyle, true);
			StyleConstants.setBold(mainStyle, false);
		} else if (fontStyle == Font.PLAIN) {
			StyleConstants.setBold(mainStyle, false);
			StyleConstants.setItalic(mainStyle, false);
		}

		// Ajout du texte
		try {
			doc.insertString(doc.getLength(), text, mainStyle);
			this.console.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet de notifier l'arrivée d'un message. Cela permet d'afficher pendant
	 * un bref instant la LED d'activité en vert pour afficher à l'utilisateur
	 * l'activité du réseau.
	 */
	public synchronized void incomingMessage () {
		// Affichage de la LED d'activité pendant un bref instant
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run () {
				lblIncomingMsgLED.setIcon(SerialLinkModel.LED_GREEN);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
				lblIncomingMsgLED.setIcon(SerialLinkModel.LED_GRAY);
			}
		});
		thread.start();
	}

	/**
	 * Permet d'afficher à l'utilisateur un message d'erreur.
	 * 
	 * @param title
	 *            Titre de la popup.
	 * @param msg
	 *            Message d'erreur.
	 */
	public void appendError (final String title, final String msg) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run () {
				JOptionPane.showMessageDialog(SerialLinkView.this, msg, title, JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	/**
	 * Permet de créer les différents éléments qui composent la vue.
	 */
	private void createView () {
		this.lblPortName = new JLabel("Port Name :");
		this.lblBaudRate = new JLabel("Baud Rate :");
		this.lblFlowControlIn = new JLabel("Flow Control In :");
		this.lblFlowControlOut = new JLabel("Flow Control Out :");
		this.lblDataBits = new JLabel("Data Bits :");
		this.lblStopBits = new JLabel("Stop Bits :");
		this.lblParity = new JLabel("Parity : ");
		this.lblRecvTimeout = new JLabel("Receive Timeout (ms) :");
		this.lblState = new JLabel("State :");
		this.lblActivity = new JLabel("Activity :");
		this.lblLedConnected = new JLabel(SerialLinkModel.LED_GRAY);
		this.lblIncomingMsgLED = new JLabel(SerialLinkModel.LED_GRAY);

		String[] baudRates =
				{"300", "1200", "2400", "4800", "9600", "14400", "19200", "28800", "38400", "57600", "115200", "230400" };
		String[] databits = {"5", "6", "7", "8" };
		String[] stopbits = {"1", "1.5", "2" };
		this.boxBaudRate = new JComboBox<>(baudRates);
		this.boxFlowControlIn = new JComboBox<>(FlowControl.values("in"));
		this.boxFlowControlOut = new JComboBox<>(FlowControl.values("out"));
		this.boxDataBits = new JComboBox<>(databits);
		this.boxStopBits = new JComboBox<>(stopbits);
		this.boxParity = new JComboBox<>(Parity.values());
		this.spRecvTimeout =
				new JSpinner(new SpinnerNumberModel(this.model.getSerialParams().getRecvTimeout(), 1, 10000, 1));

		this.btnOpenPort = new JButton("Open Port");
		this.btnClosePort = new JButton("Close Port");

		this.tfPortName = new JTextField();

		this.console = new JTextPane();
		this.console.setEditable(false);

		updateParameters(this.model.getSerialParams());
	}

	/**
	 * Permet, une fois les composants créés, de placer correctement les
	 * éléments qui composent la vue.
	 */
	private void setStyle () {
		JPanel paramsPanel = new JPanel(new MigLayout("", "[][100]20[][100]", ""));
		paramsPanel.add(this.lblPortName, "");
		paramsPanel.add(this.tfPortName, "grow");
		paramsPanel.add(this.lblBaudRate, "");
		paramsPanel.add(this.boxBaudRate, "grow, wrap");
		paramsPanel.add(this.lblFlowControlIn, "");
		paramsPanel.add(this.boxFlowControlIn, "grow");
		paramsPanel.add(this.lblFlowControlOut, "");
		paramsPanel.add(this.boxFlowControlOut, "grow, wrap");
		paramsPanel.add(this.lblDataBits, "");
		paramsPanel.add(this.boxDataBits, "grow");
		paramsPanel.add(this.lblStopBits, "");
		paramsPanel.add(this.boxStopBits, "grow, wrap");
		paramsPanel.add(this.lblParity, "");
		paramsPanel.add(this.boxParity, "grow");
		paramsPanel.add(this.lblRecvTimeout, "");
		paramsPanel.add(this.spRecvTimeout, "grow");

		JPanel mainPanel = new JPanel(new MigLayout(""));
		mainPanel.add(paramsPanel, "wrap");
		mainPanel.add(this.btnOpenPort, "split 6, span, center");
		mainPanel.add(this.btnClosePort, "");
		mainPanel.add(this.lblState, "");
		mainPanel.add(this.lblLedConnected, "");
		mainPanel.add(this.lblActivity, "");
		mainPanel.add(this.lblIncomingMsgLED, "wrap");
		mainPanel.add(new JScrollPane(this.console), "gap top 25px, grow, pushy, h 200:300");

		setContentPane(mainPanel);
	}

	/**
	 * Permet d'ajouter les différents écouteurs de la vue.
	 */
	private void setOnListeners () {
		this.btnOpenPort.addActionListener(this);
		this.btnClosePort.addActionListener(this);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		/*
		 * JButton
		 */
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();

			// Open Port
			if (this.btnOpenPort.equals(btn)) {
				this.controller.updateSerialParameters(this.tfPortName.getText(), Integer.parseInt(this.boxBaudRate
						.getSelectedItem().toString()), this.boxFlowControlIn.getSelectedItem().toString(),
						this.boxFlowControlOut.getSelectedItem().toString(), Integer.parseInt(this.boxDataBits
								.getSelectedItem().toString()), this.boxStopBits.getSelectedItem().toString(),
						this.boxParity.getSelectedItem().toString(), Integer.parseInt(this.spRecvTimeout.getValue()
								.toString()));
				this.controller.connectSerialLink();
			}
			// Close Port
			else if (this.btnClosePort.equals(btn)) {
				this.controller.disconnectSerialLink();
			}
		}
	}
}