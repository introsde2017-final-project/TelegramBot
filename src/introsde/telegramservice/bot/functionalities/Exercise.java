package introsde.telegramservice.bot.functionalities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import introsde.telegramservice.bot.LifeCoachBot;
import introsde.telegramservice.bot.model.ExerciseModel;
import introsde.telegramservice.client.BotClient;

public class Exercise {
	
	protected static final String EXERCISE = "exercise";
	public static final String GET_EXERCISE = "Get " + EXERCISE + "s";
	public static final String SEE_TODAY_EXERCISE = "See today's " + EXERCISE + "s";
	protected static final String CHOOSE_EXERCISE = "Ok, which exercise do you prefer?\n\n<b>Choose an option only if you do it!</b>\n";
	protected static final String CHOOSE_MINUTES_EXERCISE = "Ok, how many minutes did you performed ";
	protected static final String NOT_AVAILABLE_OPERATION = "<b>Operation not available now</b>\nInsert your weight and height first!\n";
	
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
			} else if (res.getStatus() == 204) {
				String firstPart = NOT_AVAILABLE_OPERATION;
				Action.sendKeyboard(bot, chatId, firstPart);
				return;
			} else {
				String firstPart = Action.ERROR;
				Action.sendKeyboard(bot, chatId, firstPart);
				return;				
			}
		}
		System.out.println("=============");

		InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
		
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
		List<InlineKeyboardButton> row = null;
		int i = 0;
		for (String exercise : exercises) {
			InlineKeyboardButton button = new InlineKeyboardButton();
			button.setText(exercise);
			button.setCallbackData(EXERCISE + "-" + exercise);
			
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
	 * Ask how many minutes the exercise was performed
	 * @param bot the bot itself
	 * @param chatId the chat id of the user
	 * @param data the callback data of the user choice
	 */
	protected static void  askMinutesExercise (LifeCoachBot bot, Long chatId, String data) {
		ForceReplyKeyboard reply = new ForceReplyKeyboard();
		
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(Exercise.CHOOSE_MINUTES_EXERCISE + data.substring(data.indexOf("-") + 1) + "?");
		message.setReplyMarkup(reply);
		
		try {
			bot.sendMessage(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve burned calories
	 * @param bot the bot itself
	 * @param chatId the chat id of the user
	 * @param text the minutes taken to perform the exercise
	 * @param reply the reply attached to the message to understand which exercise
	 */
	protected static void setPerformedExercise (LifeCoachBot bot, Long chatId, String text, String reply) {
		String exerciseName = reply.substring(reply.indexOf(CHOOSE_MINUTES_EXERCISE) + CHOOSE_MINUTES_EXERCISE.length(), reply.indexOf("?"));
		String firstPart = null;
		try { // check if the value inserted is a number
			Integer minutes = Integer.parseInt(text);
			
			ExerciseModel exercise = new ExerciseModel(exerciseName, minutes);
			Response res = BotClient.getService().path("exercise/" + chatId + "/calories").request().accept(MediaType.APPLICATION_XML).post(Entity.xml(exercise));
			
			if (res.getStatus() == 200) {
				firstPart = "Congratulations, you lost " + (res.readEntity(ExerciseModel.class)).getCalories() + " kcal\n\n<b>Well done!</b>";
			} else {
				firstPart = Action.ERROR;
			}			
		} catch (NumberFormatException e) {
			firstPart = "Sorry, not a valid number\n\n<b>Try again!</b>";
		}
		Action.sendKeyboard(bot, chatId, firstPart);
	}

	public static void getTodayExercises(LifeCoachBot bot, Long chatId) {
		
		Response res = BotClient.getService().path("exercise/" + chatId + "/today").request().accept(MediaType.APPLICATION_XML).get();
		
		String firstPart = null;
		if (res.getStatus() == 200) {
			ExerciseModel[] exercises = res.readEntity(ExerciseModel[].class);
			
			if (exercises != null) {
				Double totalCal = 0.0;
				firstPart = "<b>Today's activities:</b>\n\n";
				for (ExerciseModel e : exercises) {
					String name = e.getName();
					name = name.replace("<", "&lt;");
					name.replace(">", "&gt;");
					firstPart += "<i>" + name + "</i>\n" + "(" + e.getMinutes() + " min - " + e.getCalories() + " kcal)\n\n";
					totalCal += e.getCalories();
				}
				firstPart += "<b>Total burnt calories:</b> " + totalCal + " kcal";
			}
		} else if (res.getStatus() == 204) {
			firstPart = NOT_AVAILABLE_OPERATION;
		} else {
			firstPart = Action.ERROR;
		}
		Action.sendKeyboard(bot, chatId, firstPart);
	}

}
