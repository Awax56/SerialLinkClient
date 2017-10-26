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

import javax.comm.SerialPort;

/**
 * Classe permettant de stocker les paramètres de la liaison série.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class SerialLinkParameters {

	private String comId;
	private int baudRate;
	private int flowControlIn;
	private int flowControlOut;
	private int databits;
	private int stopbits;
	private int parity;
	private int recvTimeout;

	/**
	 * Permet d'instancier un set de paramètres par défaut (sans nom, 9600
	 * bauds, pas de flow control, 8 bits de data, 1 bit de stop, pas de
	 * parité).
	 */
	public SerialLinkParameters () {
		this("/dev/ttyUSB0", 9600, SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, 200);
	}

	/**
	 * Permet d'instancier un set de paramètres avec les valeurs spécifiées.
	 * 
	 * @param id
	 *            Identifiant du port.
	 * @param baudRate
	 *            Baud rate.
	 * @param flowControlIn
	 *            Type de contrôle de flux en réception.
	 * @param flowControlOut
	 *            Type de contrôle de flux en transmission.
	 * @param databits
	 *            Nombre de bits de data.
	 * @param stopbits
	 *            Nombre de bits de stop.
	 * @param parity
	 *            Type de parité.
	 * @param recvTimeout
	 *            Temps de timeout sur le réception en millisecondes.
	 */
	public SerialLinkParameters (String id, int baudRate, int flowControlIn, int flowControlOut, int databits,
			int stopbits, int parity, int recvTimeout) {
		this.comId = id;
		this.baudRate = baudRate;
		this.flowControlIn = flowControlIn;
		this.flowControlOut = flowControlOut;
		this.databits = databits;
		this.stopbits = stopbits;
		this.parity = parity;
		this.recvTimeout = recvTimeout;
	}

	/**
	 * Renvoie le type de flow control à partir de la constante de flow control
	 * déclarée dans la classe {@link SerialPort}.
	 * 
	 * @param flowControl
	 *            Constante décrivant de le type de contrôle de flux de la
	 *            classe {@link SerialPort}.
	 * @return Type de contrôle de flux.
	 */
	public static FlowControl getFlowControl (int flowControl) {
		switch (flowControl) {
			case SerialPort.FLOWCONTROL_NONE:
				return FlowControl.NONE;
			case SerialPort.FLOWCONTROL_RTSCTS_IN:
				return FlowControl.RTSCTS_IN;
			case SerialPort.FLOWCONTROL_XONXOFF_IN:
				return FlowControl.XONXOFF_IN;
			case SerialPort.FLOWCONTROL_RTSCTS_OUT:
				return FlowControl.RTSCTS_OUT;
			case SerialPort.FLOWCONTROL_XONXOFF_OUT:
				return FlowControl.XONXOFF_OUT;
			default:
				throw new IllegalArgumentException("Invalid flow control index : " + flowControl);
		}
	}

	/*
	 * Accesseurs
	 */

	/**
	 * Permet de spécifier l'identifiant du port.
	 * 
	 * @param portName
	 *            Identifiant du port.
	 */
	public void setComId (String id) {
		this.comId = id;
	}

	/**
	 * Renvoie l'identifiant du port.
	 * 
	 * @return Identifiant du port.
	 */
	public String getComId () {
		return this.comId;
	}

	/**
	 * Permet de spécifier le taux de transfert.
	 * 
	 * @param baudRate
	 *            Taux de transfert en bauds.
	 */
	public void setBaudRate (int baudRate) {
		this.baudRate = baudRate;
	}

	/**
	 * Renvoie le taux de transfert.
	 * 
	 * @return Taux de transfert en bauds.
	 */
	public int getBaudRate () {
		return this.baudRate;
	}

	/**
	 * Permet de spécifier le type de contrôle de flux en réception.
	 * 
	 * @param flowControl
	 *            Type de contrôle de flux en réception.
	 */
	public void setFlowControlIn (String flowControl) {
		if ("NONE".equals(flowControl)) {
			this.flowControlIn = SerialPort.FLOWCONTROL_NONE;
		} else if ("RTSCTS_IN".equals(flowControl)) {
			this.flowControlIn = SerialPort.FLOWCONTROL_RTSCTS_IN;
		} else if ("XONXOFF_IN".equals(flowControl)) {
			this.flowControlIn = SerialPort.FLOWCONTROL_XONXOFF_IN;
		} else {
			throw new IllegalArgumentException("Invalid flowControlIn value : " + flowControl);
		}
	}

	/**
	 * Renvoie le type de contrôle de flux en réception.
	 * 
	 * @return Type de contrôle de flux en réception.
	 */
	public int getFlowControlIn () {
		return this.flowControlIn;
	}

	/**
	 * Permet de spécifier le type de contrôle de flux en transmission.
	 * 
	 * @param flowControl
	 *            Type de contrôle de flux en transmission.
	 */
	public void setFlowControlOut (String flowControl) {
		if ("NONE".equals(flowControl)) {
			this.flowControlOut = SerialPort.FLOWCONTROL_NONE;
		} else if ("RTSCTS_OUT".equals(flowControl)) {
			this.flowControlOut = SerialPort.FLOWCONTROL_RTSCTS_OUT;
		} else if ("XONXOFF_OUT".equals(flowControl)) {
			this.flowControlOut = SerialPort.FLOWCONTROL_XONXOFF_OUT;
		} else {
			throw new IllegalArgumentException("Invalid flowControlOut value : " + flowControl);
		}
	}

	/**
	 * Renvoie le type de contrôle de flux en transmission.
	 * 
	 * @return Type de contrôle de flux en transmission.
	 */
	public int getFlowControlOut () {
		return this.flowControlOut;
	}

	/**
	 * Permet de spécifier le nombre de bits de data utilisés.
	 * 
	 * @param nbBits
	 *            Nombre de bits de data utilisés, voir {@link SerialPort} pour
	 *            connaître la liste des valeurs autorisées.
	 */
	public void setDatabits (int nbBits) {
		switch (nbBits) {
			case 5:
				this.databits = SerialPort.DATABITS_5;
				break;
			case 6:
				this.databits = SerialPort.DATABITS_6;
				break;
			case 7:
				this.databits = SerialPort.DATABITS_7;
				break;
			case 8:
				this.databits = SerialPort.DATABITS_8;
				break;
			default:
				throw new IllegalArgumentException("Invalid databits value : " + nbBits);
		}
	}

	/**
	 * Renvoie le nombre de bits de data utilisés.
	 * 
	 * @return Nombre de bits de data utilisés.
	 */
	public int getDatabits () {
		return this.databits;
	}

	/**
	 * Permet de spécifier le nombre de bits de stop utilisés.
	 * 
	 * @param nbBits
	 *            Nombre de bits de stop utilisés, voir {@link SerialPort} pour
	 *            connaître la liste des valeurs autorisées.
	 */
	public void setStopbits (String nbBits) {
		if (nbBits.equals("1")) {
			this.stopbits = SerialPort.STOPBITS_1;
		} else if (nbBits.equals("1.5") || nbBits.equals("1,5") || nbBits.equals("1_5")) {
			this.stopbits = SerialPort.STOPBITS_1_5;
		} else if (nbBits.equals("2")) {
			this.stopbits = SerialPort.STOPBITS_2;
		} else {
			throw new IllegalArgumentException("Invalid stopbits value : " + nbBits);
		}
	}

	/**
	 * Renvoie le nombre de bits de stop utilisés.
	 * 
	 * @return Nombre de bits de stop utilisés.
	 */
	public int getStopbits () {
		return this.stopbits;
	}

	/**
	 * Permet de spécifier le type de parité utilisée.
	 * 
	 * @param parity
	 *            Type de parité utilisée.
	 */
	public void setParity (String parity) {
		if ("NONE".equals(parity)) {
			this.parity = SerialPort.PARITY_NONE;
		} else if ("EVEN".equals(parity)) {
			this.parity = SerialPort.PARITY_EVEN;
		} else if ("MARK".equals(parity)) {
			this.parity = SerialPort.PARITY_MARK;
		} else if ("ODD".equals(parity)) {
			this.parity = SerialPort.PARITY_ODD;
		} else if ("SPACE".equals(parity)) {
			this.parity = SerialPort.PARITY_SPACE;
		} else {
			throw new IllegalArgumentException("Invalid parity value");
		}
	}

	/**
	 * Renvoie le type de parité utilisée.
	 * 
	 * @return Type de parité utilisée.
	 */
	public int getParity () {
		return this.parity;
	}

	/**
	 * Renvoie le temps en millisecondes au bout duquel le read sur la liaison
	 * série est débloqué peut importe la quantité de données acquises.
	 * 
	 * @return Temps de timeout de réception en ms.
	 */
	public int getRecvTimeout () {
		return this.recvTimeout;
	}

	/**
	 * Permet de spécifier le temps en millisecondes au bout duquel le read sur
	 * la liaison série est débloqué peut importe la quantité de données
	 * acquises.
	 * 
	 * @param tMillis
	 *            Temps de timeout de réception en ms.
	 */
	public void setRecvTimeout (int tMillis) {
		this.recvTimeout = tMillis;
	}
}