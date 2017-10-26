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
package awax.seriallink.seriallink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.CommPortOwnershipListener;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import org.apache.log4j.Logger;

/**
 * Classe permettant de gérer une liaison série, c'est-à-dire de contrôler
 * l'état de la liaison ainsi que de lire et écrire depuis celle-ci.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class SerialLinkClient implements CommPortOwnershipListener, SerialPortEventListener {

	private final Logger logger;
	private final String appName;
	private final ArrayList<SerialLinkEventListener> listeners;

	private CommPortIdentifier portId;
	private SerialPort serialPort;
	private SerialLinkParameters serialParams;
	private OutputStream output;
	private InputStream input;
	private boolean isOpen;

	/**
	 * Permet d'instancier un client de liaison série. La liaison série est
	 * paramétrée grâce à l'instance de {@link SerialLinkParameters} spécifiée
	 * puis doit être lancée via la méthode <code>SerialClient.start()</code>.
	 * Les messages entrant seront alors notifiés aux abonnés via l'interface
	 * {@link SerialLinkEventListener}.
	 * 
	 * @param appName
	 *            Nom de l'application. Ceci permet de réserver un port associé
	 *            à cette application.
	 * @param params
	 *            Instance de {@link SerialLinkParameters} contenant tous les
	 *            paramètres concernant la liaison série.
	 */
	public SerialLinkClient (final String appName, final SerialLinkParameters params) {
		this.logger = Logger.getLogger(SerialLinkClient.class);
		this.appName = appName;
		this.serialParams = params;
		this.listeners = new ArrayList<>();
		this.portId = null;
		this.serialPort = null;
		this.output = null;
		this.input = null;
		this.isOpen = false;
	}

	/**
	 * Permet d'ouvrir la connexion sur la liaison série à partir des paramètres
	 * spécifiés. Un timeout sur le portOpen est appliqué pour permettre aux
	 * autres applications de libérer le port s'il est ouvert mais plus utilisé.
	 * 
	 * @throws SerialLinkConnectionException
	 *             Si une erreur survient lors de l'ouverture de la liaison
	 *             série, une exception de type
	 *             {@link SerialLinkConnectionException} est levée.
	 */
	public void open () throws SerialLinkConnectionException {
		if (this.serialPort == null) {
			boolean error = false;
			try {
				this.logger.debug("Getting port identifier " + this.serialParams.getComId());
				this.portId = CommPortIdentifier.getPortIdentifier(this.serialParams.getComId());
				if (this.portId.getPortType() != CommPortIdentifier.PORT_SERIAL) {

					throw new SerialLinkConnectionException(this.portId.getName() + " is not a serial port");
				}
				// Permet d'ouvrir la connexion avec un timeout permettant à une
				// autre application de libérer le port si elle ne s'en sert
				// plus
				this.logger.debug("Opening port " + this.serialParams.getComId());
				this.serialPort = (SerialPort) this.portId.open(this.appName, 2000);

				// Paramètrage de la liaison série
				setParameters(this.serialParams);

				// Récupération des flux d'entrée/sortie
				this.input = this.serialPort.getInputStream();
				this.output = this.serialPort.getOutputStream();

				this.serialPort.notifyOnBreakInterrupt(true);
				this.serialPort.notifyOnDataAvailable(true);
				this.serialPort.enableReceiveTimeout(this.serialParams.getRecvTimeout());
				this.portId.addPortOwnershipListener(this);
				this.serialPort.addEventListener(this);
				this.isOpen = true;
			} catch (NoSuchPortException e) {
				error = true;
				throw new SerialLinkConnectionException(e.getMessage(), e);
			} catch (PortInUseException e) {
				error = true;
				throw new SerialLinkConnectionException(e.getMessage(), e);
			} catch (IOException e) {
				error = true;
				throw new SerialLinkConnectionException(e.getMessage(), e);
			} catch (UnsupportedCommOperationException e) {
				error = true;
				throw new SerialLinkConnectionException(e.getMessage(), e);
			} catch (TooManyListenersException e) {
				error = true;
				throw new SerialLinkConnectionException(e.getMessage(), e);
			} catch (UnsatisfiedLinkError e) {
				error = true;
				throw new SerialLinkConnectionException(
						"An error occured in the native library :\n\n" + e.getMessage(), e);
			} catch (Exception e) {
				error = true;
				throw new SerialLinkConnectionException(e.getMessage(), e);
			} finally {
				if (error && this.serialPort != null) {
					this.serialPort.close();
					this.serialPort = null;
				}
			}
		}
	}

	/**
	 * Permet de fermer la connexion série et ses éléments associés.
	 */
	public void close () {
		if (this.serialPort != null) {
			try {
				this.output.close();
				this.input.close();
			} catch (IOException e) {
				this.logger.error("An error occured while closing the serial link", e);
			}
			this.serialPort.close();
			this.serialPort = null;
			this.portId.removePortOwnershipListener(this);
		}
		this.isOpen = false;
	}

	/**
	 * Permet d'écrire le message spécifié sur la liaison série.
	 * 
	 * @param msg
	 *            Message à envoyer sur la liaison série.
	 * @throws SerialLinkConnectionException
	 *             Si une erreur survient pendant l'envoi, une exception est
	 *             levée.
	 */
	public void write (final String msg) throws SerialLinkConnectionException {
		if (this.serialPort != null && this.isOpen) {
			try {
				this.output.write(msg.getBytes());
			} catch (IOException e) {
				throw new SerialLinkConnectionException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Permet d'envoyer un signal de break de la durée spécifiée.
	 * 
	 * @param millis
	 *            Durée du break en millisecondes.
	 * @throws SerialLinkConnectionException
	 *             Si la liaison série n'est pas active, une exception est
	 *             levée.
	 */
	public void sendBreak (int millis) throws SerialLinkConnectionException {
		if (this.serialPort != null && this.isOpen) {
			this.serialPort.sendBreak(millis);
		} else {
			throw new SerialLinkConnectionException("Serial link is closed");
		}
	}

	/**
	 * Permet de paramétrer la liaison série avec les paramètres spécifiés. Si
	 * le paramétrage échoue alors les paramètres par défaut sont rétablis et
	 * une exception est levée.
	 * 
	 * @param params
	 *            Paramètres de la liaison série.
	 * @throws SerialLinkConnectionException
	 *             Si une erreur survient lors de l'ouverture de la liaison
	 *             série, une exception est levée.
	 */
	private void setParameters (final SerialLinkParameters params) throws SerialLinkConnectionException {
		// Application des paramètres
		try {
			this.serialPort.setSerialPortParams(params.getBaudRate(), params.getDatabits(), params.getStopbits(),
					params.getParity());
			int flowControl = params.getFlowControlIn() | params.getFlowControlOut();
			this.serialPort.setFlowControlMode(flowControl);
			if (this.serialPort.getFlowControlMode() != flowControl) {
				throw new SerialLinkConnectionException("Failed to update flow control mode");
			}
		} catch (UnsupportedCommOperationException e) {
			throw new SerialLinkConnectionException("Unsupported parameters for serial link", e);
		}
	}

	/**
	 * Permet de lire les messages reçus depuis la liaison série.
	 * 
	 * @return Message reçu depuis la liaison série.
	 */
	private String read () {
		StringBuffer buffer = new StringBuffer();
		int newData = 0;
		// Tant qu'il y a des données en réception
		while (newData != -1) {
			try {
				newData = this.input.read();
				if (newData == -1) {
					break;
				}
				// Substitution des \r
				if ('\r' == (char) newData) {
					buffer.append('\n');
				} else {
					buffer.append((char) newData);
				}
			} catch (IOException e1) {
				this.logger.error("An error occured while reading the serial input stream", e1);
				return null;
			}
		}
		return buffer.toString();
	}

	/*
	 * Accesseurs
	 */

	public SerialLinkParameters getSerialParams () {
		return this.serialParams;
	}

	public void setSerialParams (SerialLinkParameters serialParams) {
		this.serialParams = serialParams;
	}

	/**
	 * Permet d'ajouter un écouteur aux notifications du client de la liaison
	 * série.
	 * 
	 * @param listener
	 *            Abonné aux notifications du client de la liaison série.
	 */
	public void addSerialEventListener (final SerialLinkEventListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}

	/**
	 * Permet de retirer un écouteur aux notifications du client de la liaison
	 * série.
	 * 
	 * @param listener
	 *            Abonné aux notifications du client de la liaison série.
	 * @return Renvoie <code>true</code> si l'écouteur a été retiré,
	 *         <code>false</code> sinon.
	 */
	public boolean removeSerialEventListener (final SerialLinkEventListener listener) {
		if (listener != null && this.listeners.contains(listener)) {
			this.listeners.remove(listener);
			return true;
		}
		return false;
	}

	@Override
	public void serialEvent (SerialPortEvent e) {
		String msg = "";
		if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			// Acquisition du message
			msg = read();
		}
		// Notification des abonnés
		if (msg != null && !msg.isEmpty()) {
			for (SerialLinkEventListener listener : this.listeners) {
				listener.onNotify(new SerialLinkEvent(this, this.serialParams, msg, e.getEventType()));
			}
		}
	}

	@Override
	public void ownershipChange (int type) {
		this.logger.warn("OWNERSHIP CHANGED");
	}

	/*
	 * Accesseurs
	 */

	/**
	 * Permet de dire si la liaison série est connectée ou non.
	 * 
	 * @return <code>true</code> si la liaison série est connectée,
	 *         <code>false</code> sinon.
	 */
	public boolean isConnected () {
		return this.isOpen;
	}
}