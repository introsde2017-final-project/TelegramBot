package introsde.telegramservice.bot;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import introsde.telegramservice.bot.functionalities.Action;
import introsde.telegramservice.bot.functionalities.Exercise;
import introsde.telegramservice.bot.functionalities.Measure;
import introsde.telegramservice.bot.functionalities.Profile;
import introsde.telegramservice.bot.functionalities.Recipe;



public class LifeCoachBot extends TelegramLongPollingBot {

	public String getBotUsername() {
		return "@introsde_LifeStyle_Coach";
	}

	@Override
	public String getBotToken() {
		return "297738915:AAHygVxWpXrpdhuMDPF8hsnVZb773wlhv-I";
	}

	public void onUpdateReceived(Update update) {
		handleMessage(update);
	}

	private void handleMessage(Update update) {

		// An update could have no message (e.g. it is an inline query, or other)
		if (update.hasMessage() && update.getMessage().getText() != null) {
			String text = update.getMessage().getText(); // get the message
			Long chatId = update.getMessage().getChatId(); // get the user chat id
			

			switch (text) {
			case Action.START:
				Action.savePersonIntoDb(this, update);							
				break;
				
			case Action.HELP:
				Action.printHelp(this, update);
				break;

			// keyboard selection: update the measure
			case Measure.UPDATE_MEASURE:
				Measure.updateMeasure(this, chatId);
				break;

			// keyboard selection: search recipe
			case Recipe.SEARCH_RECIPE:
				Recipe.searchRecipe(this, chatId);
				break;

			// keyboard selection: get an exercise
			case Exercise.GET_EXERCISE:
				Exercise.getExercise(this, chatId);
				break;
				
			// keyboard selection: get today exercise
			case Exercise.SEE_TODAY_EXERCISE:
				Exercise.getTodayExercises(this, chatId);
				break;

			// keyboard selection: see profile
			case Profile.SEE_PROFILE:
				Profile.getProfile(this, chatId);
				break;

			// no keyboard selection
			default:
				Action.checkMessageNoKeyboard(this, update);	
				break;
			}

		} else { // no text, typed a button of inline keyboard
			Action.checkEmptyText(this, update);
		}

	}

}