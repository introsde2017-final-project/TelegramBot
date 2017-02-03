package introsde.telegramservice.bot.functionalities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import introsde.telegramservice.bot.LifeCoachBot;
import introsde.telegramservice.bot.model.ExerciseModel;
import introsde.telegramservice.client.BotClient;

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
		
		//Get 4 exercises from API
		List<String> exercises = new ArrayList<>();
		
		Set<Integer> exerciseIds = new HashSet<>();
		for (int i = 0; i < 4; i++) {
			//Choose exercise
			Response res = BotClient.getService().path("exercise/" + chatId).request().accept(MediaType.APPLICATION_XML).get();
			ExerciseModel ex = res.readEntity(ExerciseModel.class);
			
			if (res.getStatus() == 200) { //check that both weight and height are set
				if (!exerciseIds.contains(ex.getId())) {
					exercises.add(ex.getName());
					exerciseIds.add(ex.getId());
					System.out.println("Chosen: " + ex.getName());
				} else { //choose another exercise
					System.out.println("Already chosen: " + ex.getName());
					i--;
				}
			} else {
				String firstPart = "<b>Operation not available now</b>\nInsert your weight and height first!\n";
				Action.sendKeyboard(bot, chatId, firstPart);
				return;
			}
		}
		System.out.println("=============");

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
