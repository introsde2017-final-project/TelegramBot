package introsde.telegramservice.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Action {

	protected static final String START = "/start";
	protected static final String HELP = "/help";


	protected static final String UPDATE_FOOD = "Update food";
	protected static final String GET_RECIPE = "Get recipe";
		
	 /**
	 * Get the help
	 * @param bot the bot asking the help
	 * @param chatId the chat id of the user
	 */
	 protected static void printHelp(LifeCoachBot bot, Long chatId, String firstname) {
		 String text = "<b>Hi " + firstname+ "! I am your Life Style Coach!</b>\n" + 
				 "You can control me by using the keyboard.\n" + 
				 "\nYou can also use these commands:\n/help - Discover how to use me\n" + 
				 "/firstname - set your firstname\n" + 
				 "/lastname - set your lastname";
		 sendKeyboard(bot, chatId, text);
	 }
	
	/**
	 * Create and send the keyboard with options
	 * @param bot the bot to add the keyboard
	 * @param chatId the chat id of the user
	 * @param text the message to send with the keyboard
	 */
	protected static void sendKeyboard(LifeCoachBot bot, Long chatId, String text) {

		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(text + "\nI am ready to perform another action");

		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		// Create the keyboard (list of keyboard rows)
		List<KeyboardRow> keyboard = new ArrayList<>(); 
		// Create a keyboard row and add it
		KeyboardRow row = new KeyboardRow();
		row.add(Measure.UPDATE_MEASURE);
		row.add(UPDATE_FOOD);
		keyboard.add(row);

		row = new KeyboardRow();
		row.add(Exercise.GET_EXERCISE);
		row.add(GET_RECIPE);
		keyboard.add(row);

		// Set the keyboard to the markup
		keyboardMarkup.setKeyboard(keyboard);
		// Add it to the message
		message.setReplyMarkup(keyboardMarkup);
		message.setParseMode("html");

		try { // Send the message
			bot.sendMessage(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Check reason of a message not coming from the keyboard
	 * @param bot the bot itself
	 * @param update the message update
	 * @throws IOException
	 */
	 protected static void checkMessageNoKeyboard (LifeCoachBot bot, Update update) {
		 String firstPart = null;
		 String text = update.getMessage().getText();
		 Long chatId = update.getMessage().getChat().getId();
		 
		//if command
		if(text.startsWith("/") && text.contains(" ")) {
			String argument = text.substring(text.indexOf(" ") + 1);
			System.out.println(argument);
			
			switch (text.substring(0, text.indexOf(" "))) {
			
			case Profile.FIRSTNAME:
				Profile.setFirstname(bot, chatId, argument);
				break;
				
			case Profile.LASTNAME:
				Profile.setLastname(bot, chatId, argument);
				break;
				
			default:
				break;
			}
		} else if (update.getMessage().getReplyToMessage() != null) { //if the message is a reply
			 String reply = update.getMessage().getReplyToMessage().getText();
		
			 //if it is reply to update measure
			 if (reply.startsWith(Measure.CHOOSE_VALUE_MEASURE)) {
				 Measure.setUpdatedMeasure(bot, chatId, text, reply);
			 } else { //unrecognized reply
				 firstPart = "<b>Select action first!</b>";
				 sendKeyboard(bot, chatId, firstPart);
			 }
		 } else { //unrecognized message
			 firstPart = "<b>Select action first!</b>";
			 sendKeyboard(bot, chatId, firstPart);
		 }
		 
	 }

	/**
	 * Check the reason of an empty text of message
	 * @param bot the bot itself
	 * @param update the update message
	 */
	protected static void checkEmptyText(LifeCoachBot bot, Update update) {
		// if exists callback value --> e.g inline keyboard
		if (update.getCallbackQuery() != null) {

			String data = update.getCallbackQuery().getData();
			Long chatId = Long.valueOf(update.getCallbackQuery().getFrom().getId());
			
			try { //the callback MUST be answered --> create an empty one
				AnswerCallbackQuery answer = new AnswerCallbackQuery();
				answer.setCallbackQueryId(update.getCallbackQuery().getId());
				bot.answerCallbackQuery(answer);
			} catch (TelegramApiException e1) {
				e1.printStackTrace();
			}
			
			
			if(data.startsWith(Measure.MEASURE)) {
				Measure.askUpdatedMeasure(bot, chatId, data);
			} else if (data.startsWith(Exercise.EXERCISE)) {
				Exercise.performedExercise(bot, chatId, data);
			}
			
		}
	}
}
