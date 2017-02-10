package introsde.telegramservice.bot.functionalities;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import introsde.telegramservice.bot.LifeCoachBot;
import introsde.telegramservice.bot.model.PersonModel;
import introsde.telegramservice.bot.model.RecipeModel;
import introsde.telegramservice.client.BotClient;

public class Recipe {
	
	public static final String RECIPE = "recipe";
	public static final String SEARCH_RECIPE = "Search " + RECIPE;
	public static final String TYPE_INGREDIENT = "You have decided to search for a tasty recipe! Type an ingredient";
	public static final String CHOOSE_RECIPE = "Choose which recipe you prefer!";
	
	public static void searchRecipe(LifeCoachBot bot, Long chatId) {
		System.out.println("Searching recipe");
		
		ForceReplyKeyboard reply = new ForceReplyKeyboard();
		
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText("<b>Fantastic!</b>\n" + TYPE_INGREDIENT);
		message.setParseMode("html");
		message.setReplyMarkup(reply);
		
		try {
			bot.sendMessage(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}		
	}
	
	public static void printRecipeNames (LifeCoachBot bot, Long chatId, String text) {
		System.out.println("with " + text);
		
		Response res = BotClient.getService().path("recipe/search/" + text).request().accept(MediaType.APPLICATION_XML).get();
		
		if (res.getStatus() == 200) {
			RecipeModel[] recipes = res.readEntity(RecipeModel[].class);
			
			if (recipes.length != 0) {
				SendMessage message = new SendMessage();
				message.setChatId(chatId);
				message.setParseMode("html");
				
				message.setText(CHOOSE_RECIPE);
				
				List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
				List<InlineKeyboardButton> row = null;
				
				for (RecipeModel r : recipes) {
					InlineKeyboardButton button = new InlineKeyboardButton();
					button.setText(r.getName());
					button.setCallbackData(RECIPE + "-" + r.getId());
					
					row = new ArrayList<>(); //create new row 
					row.add(button);
					keyboard.add(row); //add ready row of one element
				}
				
				InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
	
				// Set the keyboard to the markup
				keyboardMarkup.setKeyboard(keyboard);
				// Add it to the message
				message.setReplyMarkup(keyboardMarkup);
				
				try {
					bot.sendMessage(message);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			} else {
				String firstPart = "Sorry, no result found.\n<b>Try with a different one!</b>";
				Action.sendKeyboard(bot, chatId, firstPart);
			}
		} else {
			String firstPart = Action.ERROR;
			Action.sendKeyboard(bot, chatId, firstPart);
		}
	
	}
	
	public static void sureAboutCalories(LifeCoachBot bot, Long chatId, String data) {
		System.out.println(data);
		
		Integer recipeId = Integer.parseInt(data.substring(data.indexOf("-") + 1));
		//Get sentence according to recipe calories
		Response res = BotClient.getService().path("recipe/" + recipeId + "/" + chatId).request().accept(MediaType.APPLICATION_XML).get();

		if(res.getStatus() == 200) {
			String sentence = res.readEntity(String.class);
			
			if (sentence.contains("Operation not available")) { //if calories not set, not possible
				Action.sendKeyboard(bot, chatId, sentence + " Use the command " + Profile.CALORIES_MEAL);
			} else {
				
				SendMessage message = new SendMessage();
				message.setChatId(chatId);
				message.setText(sentence);
				message.setParseMode("html");

				//send keyboard to choose whether to cook it or not
				InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
				List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
				List<InlineKeyboardButton> row = new ArrayList<>();;
				
				InlineKeyboardButton yesButton = new InlineKeyboardButton();
				yesButton.setText("Yes");
				yesButton.setCallbackData(RECIPE + "-yes-" + recipeId);
				row.add(yesButton);
				
				InlineKeyboardButton noButton = new InlineKeyboardButton();
				noButton.setText("No");
				noButton.setCallbackData(RECIPE + "-no-" + recipeId);
				row.add(noButton);
				
				keyboard.add(row);
				
				// Set the keyboard to the markup
				keyboardMarkup.setKeyboard(keyboard);
				// Add it to the message
				message.setReplyMarkup(keyboardMarkup);
				
				try {
					bot.sendMessage(message);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}
		} else {
			String firstPart = Action.ERROR;
			Action.sendKeyboard(bot, chatId, firstPart);	
		}		
	}

	public static void printURLRecipe(LifeCoachBot bot, Long chatId, String data) {
		System.out.println(data);
		
		Long recipeId = Long.parseLong(data.substring(data.lastIndexOf("-") + 1));
		Response res = BotClient.getService().path("recipe/" + recipeId).request().accept(MediaType.APPLICATION_XML).get();

		String firstPart = null;
		if (res.getStatus() == 200) {
			RecipeModel recipe = new RecipeModel();
			recipe = res.readEntity(RecipeModel.class);
			
			SendMessage message = new SendMessage();
			message.setChatId(chatId);
			message.setText("<b>" + recipe.getName() + "</b>\n" + recipe.getDescription() + "\n<a href=\"" + 
					recipe.getUrl() + "\">Go to the recipe</a>");
			message.setParseMode("html");
			
			try {
				bot.sendMessage(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
			
	        SendPhoto photoMessage = new SendPhoto();
	        photoMessage.setChatId(chatId);
	        photoMessage.setPhoto(recipe.getImage());
	        try {
	            bot.sendPhoto(photoMessage);
	        } catch (TelegramApiException e) {
	            e.printStackTrace();
	        }
			
			firstPart = "<b>Enjoy your meal!</b>\n";
		} else {
			firstPart = Action.ERROR;
		}
		Action.sendKeyboard(bot, chatId, firstPart);	
	}

}
