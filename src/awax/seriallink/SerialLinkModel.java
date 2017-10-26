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

import java.util.Observable;

import javax.swing.ImageIcon;

import awax.seriallink.seriallink.SerialLinkClient;
import awax.seriallink.seriallink.SerialLinkParameters;
import awax.seriallink.util.ResourceManager;

/**
 * Modèle de données de la fenêtre de gestion de la liaison série.
 * 
 * @author AwaX
 * @created 23 avr. 2014
 * @version 1.0
 */
public class SerialLinkModel extends Observable {

	public static final ImageIcon LED_GRAY = ResourceManager.getInstance().getImageIcon("serialLink.icon.grayLed");
	public static final ImageIcon LED_RED = ResourceManager.getInstance().getImageIcon("serialLink.icon.redLed");
	public static final ImageIcon LED_GREEN = ResourceManager.getInstance().getImageIcon("serialLink.icon.greenLed");

	private final SerialLinkParameters serialLinkParams;
	private SerialLinkClient serialLinkClient;

	/**
	 * Permet d'instancier le modèle de données par défaut.
	 * 
	 * @param serialParams
	 *            Paramètres de la liaison série.
	 */
	public SerialLinkModel (final SerialLinkParameters serialParams) {
		this.serialLinkParams = serialParams;
		this.serialLinkClient = new SerialLinkClient("SerialLink", this.serialLinkParams);
	}

	/**
	 * Permet de notifier les différents observateurs du modèle d'une
	 * modification de celui-ci.
	 * 
	 * @param arg
	 *            (Optionnel) Il est possible de spécifier un objet à
	 *            l'observateur.
	 */
	public final void notifyChanged (Object... arg) {
		this.setChanged();
		if (arg.length == 0) {
			this.notifyObservers();
		} else if (arg.length == 1 && arg[0] != null) {
			this.notifyObservers(arg[0]);
		} else {
			throw new IllegalArgumentException("Only one argument allowed");
		}
		this.clearChanged();
	}

	/*
	 * Accesseurs
	 */

	public SerialLinkParameters getSerialParams () {
		return this.serialLinkParams;
	}

	public SerialLinkClient getSerialLinkClient () {
		return this.serialLinkClient;
	}

	public void setSerialLinkClient (SerialLinkClient serialLinkClient) {
		this.serialLinkClient = serialLinkClient;
	}
}