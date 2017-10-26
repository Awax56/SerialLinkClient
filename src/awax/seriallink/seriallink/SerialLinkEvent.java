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

import javax.comm.SerialPortEvent;

/**
 * Objet émis via l'interface {@link SerialLinkEventListener} lorsq'un évènement
 * survient sur une liaison série.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public class SerialLinkEvent {

	public static final int DATA_AVAILABLE = SerialPortEvent.DATA_AVAILABLE;
	public static final int BI = SerialPortEvent.BI;
	public static final int CD = SerialPortEvent.CD;
	public static final int CTS = SerialPortEvent.CTS;
	public static final int DSR = SerialPortEvent.DSR;
	public static final int FE = SerialPortEvent.FE;
	public static final int OE = SerialPortEvent.OE;
	public static final int OUTPUT_BUFFER_EMPTY = SerialPortEvent.OUTPUT_BUFFER_EMPTY;
	public static final int PE = SerialPortEvent.PE;
	public static final int RI = SerialPortEvent.RI;

	private final SerialLinkClient source;
	private final SerialLinkParameters parameters;
	private final String message;
	private final int eventType;

	/**
	 * Permet d'instancier un évènement.
	 * 
	 * @param source
	 *            Source de l'évènement.
	 * @param params
	 *            Paramètres de la liaison série.
	 * @param msg
	 *            Message reçu depuis la liaison série s'il y en a un.
	 * @param type
	 *            Type d'évènement reçu.
	 */
	public SerialLinkEvent (SerialLinkClient source, SerialLinkParameters params, String msg, int type) {
		this.source = source;
		this.parameters = params;
		this.message = msg;
		this.eventType = type;
	}

	public SerialLinkClient getSource () {
		return this.source;
	}

	public SerialLinkParameters getParameters () {
		return this.parameters;
	}

	public String getMessage () {
		return this.message;
	}

	public int getEventType () {
		return this.eventType;
	}
}