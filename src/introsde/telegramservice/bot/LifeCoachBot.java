package introsde.telegramservice.bot;

import java.io.IOException;

import org.altervista.leocus.telegrambotutilities.EmptyUpdatesException;
import org.altervista.leocus.telegrambotutilities.ForceReply;
import org.altervista.leocus.telegrambotutilities.GettingUpdatesException;
import org.altervista.leocus.telegrambotutilities.InlineKeyboardButton;
import org.altervista.leocus.telegrambotutilities.InlineKeyboardMarkup;
import org.altervista.leocus.telegrambotutilities.Message;
import org.altervista.leocus.telegrambotutilities.ReplyKeyboardMarkup;
import org.altervista.leocus.telegrambotutilities.TelegramBot;
import org.altervista.leocus.telegrambotutilities.Update;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public final class LifeCoachBot {
	private static final String token = "<token>";
	
	private TelegramBot bot;
	
	public LifeCoachBot(String token) {
		bot = new TelegramBot(token);
	}
	

	public static void main(String[] args) throws GettingUpdatesException, IOException, EmptyUpdatesException, InterruptedException {
		LifeCoachBot coach = new LifeCoachBot(token);

		int last_update_id = 0; // last processed command
		Update[] updates = null;
		while (true) {
			updates = coach.bot.getUpdates(last_update_id + 1, 0, 0);
			last_update_id = coach.handleMessage(updates);
		}
	}
	
	private int handleMessage(Update[] updates) throws IOException, GettingUpdatesException, EmptyUpdatesException, InterruptedException {
		int updateId = 0;
		
		for (Update update : updates) { //process each message
			updateId = update.getId();
			
			// An update could have no message (e.g. it is an inline query, or other)
			if (update.getMessage() != null) {
				String text = update.getMessage().getText(); //get the message
				String chat = String.valueOf(update.getMessage().getChat().getId()); //get the user chat id
				
				switch (text) {
				case Action.START:					
				case Action.HELP:
					Action.printHelp(this.bot, chat);
					break;
				
				//keyboard selection: update the measure
				case Action.UPDATE_MEASURE:
					Action.updateMeasure(this.bot, chat);
					break;
					
				//keyboard selection: update eaten food
				case Action.UPDATE_FOOD:
					//TODO
					break;
				
				//keyboard selection: get an exercise
				case Action.GET_EXERCISE:
					//TODO
					break;
					
				//keyboard selection: get a recipe
				case Action.GET_RECIPE:
					//TODO
					break;
					
				//no keyboard selection
				default:
					Action.checkMessageNoKeyboard(this.bot, update);
					break;
				}
				
			} else { // no text, typed a button of inline keyboard
				Action.checkEmptyText(this.bot, update);
			}
		}
		return updateId;
	}
	
}