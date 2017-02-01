package introsde.telegramservice.bot.functionalities;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import introsde.telegramservice.bot.LifeCoachBot;

public class Profile {
	
	protected static final String FIRSTNAME = "/firstname";
	protected static final String LASTNAME = "/lastname";

	/**
	 * 
	 * @param bot the bot itself
	 * @param chatId the chat id of the user
	 * @param command the command you ask to type
	 */
	protected static void askName (LifeCoachBot bot, Long chatId, String command) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText("Type " + command + " followed by your " + command.substring(1));
		try {
			bot.sendMessage(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	protected static void setFirstname (LifeCoachBot bot, Long chatId, String argument) {
		//TODO save into db firstname -> argument
		String firstPart = "Ok, your new value for " + Profile.FIRSTNAME.substring(1) + " is " + argument + "\n\n<b>Well done!</b>";
		Action.sendKeyboard(bot, chatId, firstPart);
	}
	
	protected static void setLastname (LifeCoachBot bot, Long chatId, String argument) {
		//TODO save into db lastname -> argument
		String firstPart = "Ok, your new value for " + Profile.LASTNAME.substring(1) + " is " + argument + "\n\n<b>Well done!</b>";
		Action.sendKeyboard(bot, chatId, firstPart);
	}
}
