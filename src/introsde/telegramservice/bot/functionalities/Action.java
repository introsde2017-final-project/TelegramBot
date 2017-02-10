package introsde.telegramservice.bot.functionalities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import introsde.telegramservice.bot.LifeCoachBot;
import introsde.telegramservice.bot.model.PersonModel;
import introsde.telegramservice.client.BotClient;

public class Action {

	public static final String START = "/start";
	public static final String HELP = "/help";

	public static final String ERROR = "Sorry, there was an error\n";
		
	/**
	 * Save the person into the db if not exists yet
	 * @param bot the bot itself
	 * @param update the message update
	 */
	public static void savePersonIntoDb(LifeCoachBot bot, Update update) {
		String firstname = update.getMessage().getFrom().getFirstName();
		String lastname = update.getMessage().getFrom().getLastName();
		Long chatId = update.getMessage().getChatId();
		
		Action.printHelp(bot, update);

		PersonModel person = new PersonModel(firstname, lastname, chatId);
		//POST request to Process Centric Service
		BotClient.getService().path("person").request().post(Entity.xml(person));
		System.out.println("Person stored: " + person.getFirstname());
		if (lastname == null) {
			Profile.askName(bot, chatId, Profile.LASTNAME);
		}
	}
	
	
	 /**
	 * Get the help
	 * @param bot the bot asking the help
	 * @param chatId the chat id of the user
	 */
	 public static void printHelp(LifeCoachBot bot, Update update) {
		 String firstname = update.getMessage().getFrom().getFirstName();
		 Long chatId = update.getMessage().getChatId();
		 
		 String text = "<b>Hi " + firstname+ "! I am your Life Style Coach!</b>\n" + 
				 "You can control me by using the keyboard.\n" + 
				 "\nYou can also use these commands:\n/help - Discover how to use me\n" + 
				 "/firstname - Set your firstname\n" +
				 "/lastname - Set your lastname\n" +
				 "/birthday - Set your birthday\n                   dd/MM/yyyy\n" +
				 "/email - Set your e-mail\n" +
				 "/meal_kcal - Set the maximum kcal for your meal\n";
		 sendKeyboard(bot, chatId, text);
	 }
	
	/**
	 * Create and send the keyboard with options
	 * @param bot the bot to add the keyboard
	 * @param chatId the chat id of the user
	 * @param text the message to send with the keyboard
	 */
	public static void sendKeyboard(LifeCoachBot bot, Long chatId, String text) {

		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(text);

		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		// Create the keyboard (list of keyboard rows)
		List<KeyboardRow> keyboard = new ArrayList<>(); 
		// Create a keyboard row and add it
		KeyboardRow row = new KeyboardRow();
		row.add(Profile.SEE_PROFILE);
		row.add(Measure.UPDATE_MEASURE);
		keyboard.add(row);

		row = new KeyboardRow();
		row.add(Exercise.SEE_TODAY_EXERCISE);
		row.add(Exercise.GET_EXERCISE);
		keyboard.add(row);
		
		row = new KeyboardRow();
		row.add(Recipe.SEARCH_RECIPE);
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
	 public static void checkMessageNoKeyboard (LifeCoachBot bot, Update update) {
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
				
			case Profile.BIRTHDAY:
				Profile.setBirthDate(bot, chatId, argument);
				break;
			
			case Profile.EMAIL:
				Profile.setEmail(bot, chatId, argument);
				break;
				
			case Profile.CALORIES_MEAL:
				Profile.setCaloriesMeal(bot, chatId, argument);
				break;
				
			default:
				break;
			}
		} else if (update.getMessage().getReplyToMessage() != null) { //if the message is a reply
			 String reply = update.getMessage().getReplyToMessage().getText();
		
			 //if it is reply to update measure
			 if (reply.startsWith(Measure.CHOOSE_VALUE_MEASURE)) {
				 Measure.setUpdatedMeasure(bot, chatId, text, reply);
			 } else if (reply.startsWith(Exercise.CHOOSE_MINUTES_EXERCISE)) { //if it is reply to set minutes
				 Exercise.setPerformedExercise(bot, chatId, text, reply);
			 } else if (reply.endsWith(Recipe.TYPE_INGREDIENT)) { //if it is reply to search recipes
				 Recipe.printRecipeNames(bot, chatId, text);
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
	public static void checkEmptyText(LifeCoachBot bot, Update update) {
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
				Exercise.askMinutesExercise(bot, chatId, data);
			} else if (data.startsWith(Recipe.RECIPE)) {
				if (data.contains("yes")) {
					Recipe.printURLRecipe(bot, chatId, data);
				} else if (data.contains("no")){
					Action.sendKeyboard(bot, chatId, "Ok, choose another recipe.");
				} else {
					Recipe.sureAboutCalories(bot, chatId, data);
				}
			}
			
		}
	}
}
