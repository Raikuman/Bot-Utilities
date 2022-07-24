package com.raikuman.botutilities.configs;

import com.raikuman.botutilities.crypto.AESEncryption;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import io.github.cdimascio.dotenv.DotenvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

/**
 * Provides a function to load a .env file to get environment variables
 *
 * @version 2.0 2022-24-07
 * @since 1.0
 */
public class EnvLoader {

	private static final Logger logger = LoggerFactory.getLogger(EnvLoader.class);
	private static Dotenv dotenv;

	/**
	 * Loads .env file using location from config. If no config file location is found, user will be
	 * prompted for the location and filename of the .env file. If the config file location is incorrect,
	 * then the default Dotenv will be used.
	 */
	public static void loadEnv() {
		if (ConfigIO.readConfig("settings", "env") == null)
			updateEnvLocation();
		else
			findEnv();
	}

	/**
	 * Returns current the Dotenv object
	 * @return The Dotenv object
	 */
	private static Dotenv getDotenv() {
		if (dotenv == null) {
			dotenv = Dotenv.load();
			return dotenv;
		}

		return dotenv;
	}

	/**
	 * Tries to find the .env file location and set Dotenv to that location
	 */
	private static void findEnv() {
		String envLoc = ConfigIO.readConfig("settings", "env");
		if (envLoc == null) {
			logger.warn("No .env file location found in settings, use default");
			dotenv = getDotenv();
			return;
		}

		File file = new File(decryptEnvLoc(envLoc));
		try {
			dotenv = new DotenvBuilder()
				.directory(file.getPath())
				.filename(file.getName())
				.load();
		} catch (DotenvException e) {
			logger.warn("Could not load .env from specified config: " + envLoc + ", use default");
			dotenv = getDotenv();
		}
	}

	/**
	 * Updates the location of the .env file to the bot settings config and set Dotenv to that location
	 */
	private static void updateEnvLocation() {
		Scanner scanner = new Scanner(System.in);

		boolean accessEnv = false;
		String envLocation = null, envFileName = null;
		while (!accessEnv) {
			System.out.println("No directory found for .env file. Provide directory and .env name");
			System.out.print("Directory: ");
			envLocation = scanner.nextLine();

			System.out.print(".env file name: ");
			envFileName = scanner.nextLine();
			if (envFileName.contains(".env"))
				envFileName = envFileName.replace(".env", "");

			envFileName += ".env";
			try {
				dotenv = new DotenvBuilder()
					.directory(envLocation)
					.filename(envFileName)
					.load();

				accessEnv = true;

				System.out.print("\033[H\033[2J");
				System.out.flush();
			} catch (DotenvException e) {
				logger.error("No .env found at location: " + envLocation);
				return;
			}
		}

		if (envLocation == null) {
			logger.error("Could not get location for .env");
			return;
		}

		String fileLocation = envLocation + "\\" + envFileName;
		ConfigIO.overwriteConfig("settings", "env", encryptEnvLoc(fileLocation));
	}

	/**
	 * Encrypts the location of the .env file
	 * @param input Location string to encrypt
	 * @return The encrypted location string
	 */
	private static String encryptEnvLoc(String input) {
		try {
			SecretKey secretKey = AESEncryption.generateKey(128);
			if (secretKey == null) {
				logger.error("Could not generate secret key");
				return "";
			}

			IvParameterSpec ivParameterSpec = AESEncryption.generateIv();
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

			byte[] cipheredArray = cipher.doFinal(input.getBytes());
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try {
				output.write(ivParameterSpec.getIV());
				output.write(secretKey.getEncoded());
				output.write(cipheredArray);
			} catch (IOException e) {
				logger.error("Could not write .env location to byte stream");
				return "";
			}

			return Base64.getEncoder().encodeToString(output.toByteArray());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			logger.error("No algorithm or padding found");
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			logger.error("Invalid key or algorithm parameter");
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			logger.error("Illegal block size or bad padding");
		}

		logger.error("Could not encrypt .env location");
		return "";
	}

	/**
	 * Decrypts the location of the .env file
	 * @param cipherText Location string to decrypt
	 * @return The decrypted location string
	 */
	private static String decryptEnvLoc(String cipherText) {
		try {
			byte[] cipheredArray = Base64.getDecoder().decode(cipherText.getBytes());
			byte[] foundIv = Arrays.copyOfRange(cipheredArray, 0, 16);
			byte[] foundKey = Arrays.copyOfRange(cipheredArray, 16, 32);
			byte[] foundText = Arrays.copyOfRange(cipheredArray, 32, cipheredArray.length);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(
				Cipher.DECRYPT_MODE,
				new SecretKeySpec(foundKey, "AES"),
				new IvParameterSpec(foundIv)
			);

			return new String(cipher.doFinal(foundText));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			logger.error("No algorithm or padding found");
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			logger.error("Invalid key or algorithm parameter");
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			logger.error("Illegal block size or bad padding");
		}

		logger.error("Could not decrypt .env location");
		return "";
	}

	/**
	 * Returns the value of a given environment variable in a .env file
	 * @param key The name of the key to look for
	 * @return The value of the key
	 */
	public static String get(String key) {
		return getDotenv().get(key.toUpperCase());
	}
}
