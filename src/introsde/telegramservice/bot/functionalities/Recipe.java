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
			
			SendMessage message = new SendMessage();
			message.setChatId(chatId);
			message.setParseMode("html");
			
			if (recipes.length != 0) {
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
			} else {
				message.setText("Sorry, no result found.\n<b>Try with a different one!</b>");
			}
			
			try {
				bot.sendMessage(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		} else {
			String firstPart = Action.ERROR;
			Action.sendKeyboard(bot, chatId, firstPart);
		}
	
	}
	
	public static void sureAboutCalories(LifeCoachBot bot, Long chatId, String data) {
		System.out.println(data);
		
		Integer recipeId = Integer.parseInt(data.substring(data.indexOf("-") + 1));
		Response res = BotClient.getService().path("recipe/" + recipeId).request().accept(MediaType.APPLICATION_XML).get();

		if (res.getStatus() == 200) {
			RecipeModel recipe = new RecipeModel();
			recipe = res.readEntity(RecipeModel.class);
			
			Double goalCalories = 200.0;
			SendMessage message = new SendMessage();
			message.setChatId(chatId);
			
			Integer percentage = (int)((recipe.getCalories()/goalCalories)*100);
			String messageText = "<b>" + recipe.getName() + "</b> is equal to the " + percentage + "% of your meal calories goal.\n" + 
					"It also contains:\n   * carbohydrate: " + recipe.getCarbohydrate() + "\n" + 
					"   * fat: " + recipe.getFat() + "\n   * protein: " + recipe.getProtein() + "\n\n";
			
			if(percentage > 100) {
				messageText += "Are you <b>really</b> sure to cook it?";
			} else {
				messageText += "<b>Perfect!</b>\nReady to cook it?";
			}
			message.setText(messageText);
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
