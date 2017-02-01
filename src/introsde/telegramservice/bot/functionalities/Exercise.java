package introsde.telegramservice.bot.functionalities;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import introsde.telegramservice.bot.LifeCoachBot;

public class Exercise {
	
	protected static final String EXERCISE = "exercise";
	public static final String GET_EXERCISE = "Get " + EXERCISE;
	protected static final String CHOOSE_EXERCISE = "Ok, which exercise do you prefer?\n\n<b>Choose an option only if you do it!</b>\n";

	
	/**
	 * Get exercises to perform after choosing the option from the keyboard
	 * @param bot the bot itself
	 * @param chatId chatId the chat id of the user
	 */
	public static void getExercise(LifeCoachBot bot, Long chatId) {
		//send message asking which exercise to perform
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(CHOOSE_EXERCISE);
		
		InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
		
		//TODO Get 4 exercises from API and how much time
		List<String> exercises = new ArrayList<>();
		exercises.add("Walking");
		exercises.add("Aerobics");
		exercises.add("Martial Arts");
		exercises.add("Squats (Legs)");
		String minutes = "5";
		
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
		List<InlineKeyboardButton> row = null;
		int i = 0;
		for (String exercise : exercises) {
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText(exercise + "\n" + minutes + " min");
			button.setCallbackData(EXERCISE + "-" + exercise + "-" + minutes);
			
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
	
	protected static void  performedExercise (LifeCoachBot bot, Long chatId, String data) {
		String[] tokens = data.split("-"); //format -> exercise-activity-minutes
		
		//TODO get calories based on activity and minutes
		int calories = 100;
		
		String firstPart = "Congratulations, you lost " + calories + " calories\n\n<b>Well done!</b>";
		Action.sendKeyboard(bot, chatId, firstPart);
	}

}
