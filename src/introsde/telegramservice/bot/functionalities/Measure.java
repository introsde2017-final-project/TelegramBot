package introsde.telegramservice.bot.functionalities;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import introsde.telegramservice.bot.LifeCoachBot;
import introsde.telegramservice.bot.model.MeasureModel;
import introsde.telegramservice.client.BotClient;

public class Measure {

	protected static final String MEASURE = "measure";
	public static final String UPDATE_MEASURE = "Update " + MEASURE;
	protected static final String CHOOSE_MEASURE = "Ok, which measure do you want to update?\n<b>Choose an option</b>";
	protected static final String CHOOSE_VALUE_MEASURE = "Ok, which is your new value for ";

	
	 /**
	 * Get next operation after having chosen to update a measure
	 * @param bot the bot itself
	 * @param chatId the chat id of the user
	 */
	public static void updateMeasure(LifeCoachBot bot, Long chatId) {
		
		//send message asking which measure to update
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(CHOOSE_MEASURE);
		
		InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
		
		//TODO Get list of measure from db
		List<String> measures = new ArrayList<>();
		measures.add("height");
		measures.add("weight");
		
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
		List<InlineKeyboardButton> row = null;
		int i = 0;
		for (String measure : measures) {
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText(measure);
			button.setCallbackData(MEASURE + "-" + measure);
			
			if((i % 2) == 0) {
				row = new ArrayList<>(); //create new row every 2 elements
				row.add(button);
			} else {
				row.add(button);
				keyboard.add(row); //add ready row of two elements
			}
			i++;
		}
		
		// Set the keyboard to the markup
		keyboardMarkup.setKeyboard(keyboard);
		// Add it to the message
		message.setReplyMarkup(keyboardMarkup);
		message.setParseMode("html");
		
		try {
			bot.sendMessage(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ask which is the new value for the measure
	 * @param bot the bot itself
	 * @param chatId the chat id of the user
	 * @param data the callback data of the user choice
	 */
	protected static void askUpdatedMeasure (LifeCoachBot bot, Long chatId, String data) {
		ForceReplyKeyboard reply = new ForceReplyKeyboard();
		
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(Measure.CHOOSE_VALUE_MEASURE + data.substring(data.indexOf("-") + 1) + "?");
		message.setReplyMarkup(reply);
		
		try {
			bot.sendMessage(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Save the new value for the measure
	 * @param bot the bot itself
	 * @param chatId chatId the chat id of the user
	 * @param text the new value
	 * @param reply the reply attached to the message to understand which measure
	 */
	protected static void setUpdatedMeasure (LifeCoachBot bot, Long chatId, String text, String reply) {
		String measure = reply.substring(reply.indexOf(CHOOSE_VALUE_MEASURE) + CHOOSE_VALUE_MEASURE.length(), reply.indexOf("?"));
		String firstPart = null;
		try { // check if the value inserted is a number
			Double num = Double.parseDouble(text);

			MeasureModel measureModel = new MeasureModel(text, measure, "double");
			Response res = BotClient.getService().path("measure/" + chatId).request().post(Entity.xml(measureModel));

			if (res.getStatus() == 200) {
				firstPart = "Ok, your new value for " + measure + " is " + text + "\n\n<b>Well done!</b>";
			} else {
				firstPart = "Sorry, there was an error\n";
			}
		} catch (NumberFormatException e) {
			firstPart = "Sorry, not a valid number<b>\n\nTry again!</b>";
		}
		Action.sendKeyboard(bot, chatId, firstPart);
	}

}
