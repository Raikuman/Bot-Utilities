package com.raiku.botutilities.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Gets the local date and time and provides a method to format a long
 *
 * @version 1.0 2022-17-06
 * @since 1.0
 */
public class DateAndTime {

	/**
	 * Returns the current time of the bot
	 * @return The time as a string
	 */
	public static String getTime() {
		DateFormat time = new SimpleDateFormat("hh:mm aa");
		return time.format(new Date());
	}

	/**
	 * Returns the current date of the bot
	 * @return The date as a string
	 */
	public static String getDate() {
		DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
		return date.format(new Date());
	}

	/**
	 * Returns a formatted string given a long
	 * @param timeInMillis The time as a long
	 * @return The formatted string
	 */
	public static String formatMilliseconds(long timeInMillis) {
		return String.format("%02d:%02d:%02d",
			TimeUnit.MILLISECONDS.toHours(timeInMillis),
			TimeUnit.MILLISECONDS.toMinutes(timeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillis)),
			TimeUnit.MILLISECONDS.toSeconds(timeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
		);
	}
}
