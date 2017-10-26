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

import java.util.Vector;

/**
 * Permet de spécifier les différents types de contrôle de flux.
 * 
 * @author AwaX
 * @created 28 avr. 2014
 * @version 1.0
 */
public enum FlowControl {
	NONE, RTSCTS_IN, XONXOFF_IN, RTSCTS_OUT, XONXOFF_OUT;

	/**
	 * Renvoie la liste des types de contrôle de flux en entrée ou en sortie.
	 * 
	 * @param inOut
	 *            "in" pour les types en entrée ou "out" pour les types en
	 *            sortie.
	 * @return Liste des types de contrôle de flux en entrée ou en sortie.
	 */
	public static Vector<FlowControl> values (final String inOut) {
		Vector<FlowControl> values = new Vector<>();
		if (inOut.equals("in")) {
			values.add(NONE);
			values.add(RTSCTS_IN);
			values.add(XONXOFF_IN);
			return values;
		} else if (inOut.equals("out")) {
			values.add(NONE);
			values.add(RTSCTS_OUT);
			values.add(XONXOFF_OUT);
			return values;
		} else {
			return null;
		}
	}
}