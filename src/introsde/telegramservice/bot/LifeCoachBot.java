package introsde.telegramservice.bot;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;


public class LifeCoachBot extends TelegramLongPollingBot {

	@Override
	public String getBotUsername() {
		return "@introsde_LifeStyle_Coach";
	}

	@Override
	public String getBotToken() {
		return "297738915:AAHygVxWpXrpdhuMDPF8hsnVZb773wlhv-I";
	}

	@Override
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
			case Action.HELP:
				Action.printHelp(this, chatId);
				break;

			// keyboard selection: update the measure
			case Action.UPDATE_MEASURE:
				Action.updateMeasure(this, chatId);
				break;

			// keyboard selection: update eaten food
			case Action.UPDATE_FOOD:
				// TODO
				break;

			// keyboard selection: get an exercise
			case Action.GET_EXERCISE:
				// TODO
				break;

			// keyboard selection: get a recipe
			case Action.GET_RECIPE:
				// TODO
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