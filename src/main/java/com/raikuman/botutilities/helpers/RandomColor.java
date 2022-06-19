package com.raikuman.botutilities.helpers;

import java.awt.*;
import java.util.Random;

/**
 * Provides method to randomly generate a color object
 *
 * @version 1.0 2022-17-06
 * @since 1.0
 */
public class RandomColor {

	private final static Random random = new Random();

	/**
	 * Creates a randomized Color object
	 * @return A random color
	 */
	public static Color getRandomColor() {
		float r = random.nextFloat();
		float g = random.nextFloat();
		float b = random.nextFloat();

		return new Color(r, g, b);
	}
}
