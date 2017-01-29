package introsde.telegramservice.bot;

import java.io.IOException;

import org.altervista.leocus.telegrambotutilities.EmptyUpdatesException;
import org.altervista.leocus.telegrambotutilities.ForceReply;
import org.altervista.leocus.telegrambotutilities.GettingUpdatesException;
import org.altervista.leocus.telegrambotutilities.InlineKeyboardButton;
import org.altervista.leocus.telegrambotutilities.InlineKeyboardMarkup;
import org.altervista.leocus.telegrambotutilities.ReplyKeyboardMarkup;
import org.altervista.leocus.telegrambotutilities.TelegramBot;
import org.altervista.leocus.telegrambotutilities.Update;

public class Action {
	
	protected static final String START = "/start";
	protected static final String HELP = "/help";

	
	protected static final String UPDATE_MEASURE = "Update measure";
	protected static final String UPDATE_FOOD = "Update food";
	protected static final String GET_EXERCISE = "Get exercise";
	protected static final String GET_RECIPE = "Get recipe";
	
	private static final String CHOOSE_MEASURE = "Ok, which measure do you want to update?";
	private static final String CHOOSE_VALUE_MEASURE = "Ok, which is your new value for ";

	/**
	 * Get the help
	 * @param bot the bot asking the help
	 * @param chat the chat id (string) of the user
	 * @throws IOException
	 */
	protected static void printHelp(TelegramBot bot, String chat) throws IOException {
		String message = "<b>Hi! I am your Life Style Coach!</b>\nYou can control me by using the keyboard.";
		createKeyword(bot, chat, message);
	}
	
	/**
	 * Create a keyboard with options
	 * @param bot the bot to add the keyboard
	 * @param chat the chat id (string) of the user
	 * @param message the message to send with the keyboard
	 * @throws IOException
	 */
	protected static void createKeyword(TelegramBot bot, String chat, String message) throws IOException {
		String[][] keys = new String[2][];
		keys[0] = new String[2];
		keys[0][0] = UPDATE_MEASURE;
		keys[0][1] = UPDATE_FOOD;
		keys[1] = new String[2];
		keys[1][0] = GET_EXERCISE;
		keys[1][1] = GET_RECIPE;
		ReplyKeyboardMarkup reply_markup = new ReplyKeyboardMarkup(keys);
		bot.sendMessage(chat, message, "html", false, false, 0, reply_markup);
	}
	
	/**
	 * Get next operation after having chosen to update a measure
	 * @param bot the bot itself
	 * @param chat the chat id (string) of the user
	 * @throws IOException
	 * @throws GettingUpdatesException
	 * @throws EmptyUpdatesException
	 */
	protected static void updateMeasure(TelegramBot bot, String chat) throws IOException, GettingUpdatesException, EmptyUpdatesException {
		bot.sendMessage(chat, CHOOSE_MEASURE);
		InlineKeyboardButton[][] buttons = new InlineKeyboardButton[1][2];

		//TODO Get list of measure from db
		buttons[0][0] = new InlineKeyboardButton("Weight", null, "weight", null);
		buttons[0][1] = new InlineKeyboardButton("Height", null, "height", null);

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(buttons);
		bot.sendMessage(chat, "<b>Choose an option</b>", "html", false, false, 0, inlineKeyboardMarkup);
	}
	
	/**
	 * Check reason of a message not coming from the keyboard
	 * @param bot the bot itself
	 * @param update the message update
	 * @throws IOException
	 */
	protected static void checkMessageNoKeyboard (TelegramBot bot, Update update) throws IOException {
		String firstPart = null;
		String text = update.getMessage().getText();
		String chat = String.valueOf(update.getMessage().getChat().getId());
		
		//if the message is a reply
		if (update.getMessage().getReply_to_message() != null) {
			String data = update.getMessage().getReply_to_message().getText();
			
			//if it is reply to update measure
			if (data.startsWith(CHOOSE_VALUE_MEASURE)) {
				String measure = data.substring(data.indexOf(CHOOSE_VALUE_MEASURE) + CHOOSE_VALUE_MEASURE.length(), data.indexOf("?"));

				try { //check if the value inserted is a number
					Double num = Double.parseDouble(text);
					bot.sendMessage(chat, "Ok, your new value for " + measure + " is " + text);
					//TODO save new value num for measure
					firstPart = "<b>Well done!</b>\n";
				} catch (NumberFormatException e) {
					bot.sendMessage(chat, "Sorry, not a valid number");
					firstPart = "<b>Try again!</b>\n";
				}
			} else { //unrecognized reply
				firstPart = "<b>Select action!</b>\n";
			}
		} else { //unrecognized message
			firstPart = "<b>Select action!</b>\n";
		}
		createKeyword(bot, chat, firstPart + "\nI am ready to perform another action");
	}
	
	
	/**
	 * Check the reason of an empty text of message
	 * @param bot the bot itself
	 * @param update the update message
	 * @throws IOException
	 */
	protected static void checkEmptyText(TelegramBot bot, Update update) throws IOException {
		//if exists callback value --> e.g inline keyboard
		if (update.getCallbackQuery() != null) {
			String data = update.getCallbackQuery().getData();
			String chat = String.valueOf(update.getCallbackQuery().getFrom().getId());

			bot.sendMessage(chat, CHOOSE_VALUE_MEASURE + data + "?", null, false, false, 0, new ForceReply(false));
		}
	}
}
