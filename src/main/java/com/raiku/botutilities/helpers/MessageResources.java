package com.raiku.botutilities.helpers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Duration;

/**
 * Provides methods for commonly used messages for JDA
 *
 * @version 1.0 2022-17-06
 * @since 1.0
 */
public class MessageResources {

	/**
	 * Sends a timed message to a specified text channel
	 * @param message The message string to send
	 * @param channel The channel to send the message to
	 * @param numSeconds The number of seconds the message will be visible
	 */
	public static void timedMessage(String message, TextChannel channel, int numSeconds) {
		channel.sendMessage(message)
			.delay(Duration.ofSeconds(numSeconds))
			.flatMap(Message::delete)
			.queue();
	}

	/**
	 * Sends a timed voice channel connection error to a specified text channel
	 * @param channel The channel to send the message to
	 * @param numSeconds The number of seconds the message will be visible
	 */
	public static void connectError(TextChannel channel, int numSeconds) {
		String msg = "Could not connect to your channel. Please contact a server admin about this issue.";
		channel.sendMessage(msg)
			.delay(Duration.ofSeconds(numSeconds))
			.flatMap(Message::delete)
			.queue();
	}
}
