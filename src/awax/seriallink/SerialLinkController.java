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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import awax.seriallink.seriallink.SerialLinkClient;
import awax.seriallink.seriallink.SerialLinkConnectionException;
import awax.seriallink.seriallink.SerialLinkEvent;
import awax.seriallink.seriallink.SerialLinkEventListener;
import awax.seriallink.seriallink.SerialLinkParameters;

/**
 * Contrôleur de la fenêtre de gestion de la liaison série.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class SerialLinkController implements SerialLinkEventListener {

	private final SerialLinkModel model;
	private final SerialLinkView view;
	private final Logger logger;

	/**
	 * Permet d'intancier le contrôleur de la fenêtre de gestion de la liaison
	 * série.
	 * 
	 * @param model
	 *            Modèle de données associé.
	 */
	public SerialLinkController (final SerialLinkModel model) {
		this.model = model;
		this.view = new SerialLinkView(model, this);
		this.logger = Logger.getLogger(SerialLinkController.class);
	}

	@Override
	public void onNotify (SerialLinkEvent event) {
		String msg = event.getMessage();
		this.view.incomingMessage();

		// Réception d'un message
		if (event.getEventType() == SerialLinkEvent.DATA_AVAILABLE) {
			this.logger.info("Message received : " + msg);
			appendConsole("Message received : " + msg, Color.green.darker());
		} else {
			switch (event.getEventType()) {
				case SerialLinkEvent.BI:
					this.logger.info("BREAK INTERRUPT received");
					break;
				case SerialLinkEvent.CD:
					this.logger.info("CARRIER DETECT received");
					break;
				case SerialLinkEvent.CTS:
					this.logger.info("CLEAR TO SEND received");
					break;
				case SerialLinkEvent.DSR:
					this.logger.info("DATA SET READY received");
					break;
				case SerialLinkEvent.FE:
					this.logger.info("FRAMING ERROR received");
					break;
				case SerialLinkEvent.OE:
					this.logger.info("OVERRUN ERROR received");
					break;
				case SerialLinkEvent.OUTPUT_BUFFER_EMPTY:
					this.logger.info("OUTPUT BUFFER EMPTY received");
					break;
				case SerialLinkEvent.PE:
					this.logger.info("PARITY ERROR received");
					break;
				case SerialLinkEvent.RI:
					this.logger.info("RING INDICATOR received");
					break;
				default:
					this.logger.error("Receiving an unknown command type");
					break;
			}
		}
	}

	/**
	 * Permet d'afficher l'interface graphique de l'application.
	 */
	public void showGui () {
		this.view.showGui();
	}

	/**
	 * Permet de connecter la liaison série avec le PSAC.
	 */
	public void connectSerialLink () {
		final SerialLinkParameters serialParams = this.model.getSerialParams();
		final SerialLinkClient client = this.model.getSerialLinkClient();
		// Tentative de connexion
		if (client != null) {
			if (!client.isConnected()) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run () {
						try {
							logger.info("Opening " + serialParams.getComId() + "...");
							appendConsole("Opening " + serialParams.getComId() + "...", Color.blue);
							client.setSerialParams(model.getSerialParams());
							client.open();
							client.addSerialEventListener(SerialLinkController.this);

							// Si la connexion a réussie
							if (client.isConnected()) {
								logger.info(serialParams.getComId() + " now connected");
								appendConsole(serialParams.getComId() + " now connected", Color.blue);
								view.setConnected(client.isConnected());
							} else {
								logger.error(serialParams.getComId() + " connection failed");
								appendConsole(serialParams.getComId() + " connection failed", Color.red);
								view.appendError("Connection failed", "Connection to port "
										+ client.getSerialParams().getComId() + " failed.");
							}
						} catch (SerialLinkConnectionException e) {
							logger.error("Connection to " + client.getSerialParams().getComId() + " failed", e);
							appendConsole("Connection to " + client.getSerialParams().getComId() + " failed", Color.red);
							view.appendError("Connection failed", "Connection to port "
									+ client.getSerialParams().getComId() + " failed.\n\n" + e.getMessage());
						}
					}
				});
			}
		} else {
			throw new NullPointerException("Serial link client cannot be null");
		}
	}

	/**
	 * Permet de déconnecter la liaison série.
	 */
	public void disconnectSerialLink () {
		SerialLinkParameters serialParams = this.model.getSerialParams();
		SerialLinkClient client = this.model.getSerialLinkClient();
		if (client != null) {
			client.close();
			client.removeSerialEventListener(this);
			this.logger.info(serialParams.getComId() + " is now disconnected");
			appendConsole(serialParams.getComId() + " is now disconnected", Color.blue);
			this.view.setConnected(client.isConnected());
		}
	}

	/**
	 * Permet d'envoyer un message sur la liaison série.
	 * 
	 * @param msg
	 *            Message à envoyer sur la liaison série.
	 * @throws SerialLinkConnectionException
	 *             Si une erreur survient pendant l'envoi, une exception est
	 *             lancée.
	 */
	public void sendMessage (String msg) throws SerialLinkConnectionException {
		SerialLinkParameters serialParams = this.model.getSerialParams();
		SerialLinkClient client = this.model.getSerialLinkClient();
		// Si le client est connecté
		if (client != null && client.isConnected()) {
			this.logger.info("Sending a message on " + serialParams.getComId());
			appendConsole("Sending a message on " + serialParams.getComId(), Color.green.darker());
			client.write(msg);
		} else {
			throw new SerialLinkConnectionException("Client disconnected");
		}
	}

	/**
	 * Permet d'afficher du texte dans la console de log.
	 * 
	 * @param text
	 *            Texte à afficher.
	 * @param color
	 *            Couleur du texte à afficher.
	 */
	public void appendConsole (String text, Color color) {
		SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
		this.view.appendConsole(time.format(new Date()) + " ", Color.black, Font.BOLD);
		this.view.appendConsole(text + "\n", color, Font.PLAIN);
	}

	/**
	 * Permet de mettre à jour les paramètres de la liaison série.
	 * 
	 * @param comId
	 *            Identifiant du port de la liaison série.
	 * @param baudRate
	 *            Taux de transfert en bauds.
	 * @param flowControlIn
	 *            Type de contrôle de flux en entrée.
	 * @param flowControlOut
	 *            Type de contrôle de flux en sortie.
	 * @param databits
	 *            Nombre de bits de data.
	 * @param stopbits
	 *            Nombre de bits de stop.
	 * @param parity
	 *            Type de parité.
	 * @param recvTimeout
	 *            Temps de timeout sur la réception.
	 */
	public void updateSerialParameters (String comId, int baudRate, String flowControlIn, String flowControlOut,
			int databits, String stopbits, String parity, int recvTimeout) {
		SerialLinkParameters params = this.model.getSerialParams();
		params.setComId(comId);
		params.setBaudRate(baudRate);
		params.setFlowControlIn(flowControlIn);
		params.setFlowControlOut(flowControlOut);
		params.setDatabits(databits);
		params.setStopbits(stopbits);
		params.setParity(parity);
		params.setRecvTimeout(recvTimeout);
	}

	/*
	 * Accesseurs
	 */

	/**
	 * Renvoie la fenêtre de gestion de la liaison série.
	 * 
	 * @return Fenêtre de gestion de la liaison série.
	 */
	public SerialLinkView getView () {
		return this.view;
	}
}