package introsde.telegramservice.bot.functionalities;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import introsde.telegramservice.bot.LifeCoachBot;
import introsde.telegramservice.bot.model.MeasureModel;
import introsde.telegramservice.bot.model.PersonModel;
import introsde.telegramservice.client.BotClient;

public class Profile {

	protected static final String FIRSTNAME = "/firstname";
	protected static final String LASTNAME = "/lastname";
	protected static final String BIRTHDAY = "/birthday";
	protected static final String EMAIL = "/email";
	protected static final String CALORIES_MEAL = "/meal_kcal";
	protected static final String SLEEPING_TIME = "/sleep_hours";
	public static final String SEE_PROFILE = "See profile";

	/**
	 *
	 * @param bot the bot itself
	 * @param chatId the chat id of the user
	 * @param command the command you ask to type
	 */
	protected static void askName (LifeCoachBot bot, Long chatId, String command) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText("Type " + command + " followed by your " + command.substring(1));
		try {
			bot.sendMessage(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private static void setName (LifeCoachBot bot, Long chatId, String argument, String command) {
		System.out.println("Setting " + command);

		PersonModel person = new PersonModel();
		if (command.equals(Profile.FIRSTNAME)) {
			person.setFirstname(argument);
		} else if (command.equals(Profile.LASTNAME)) {
			person.setLastname(argument);
		} else if (command.equals(Profile.BIRTHDAY)) {
			person.setBirthdate(argument);
		} else if (command.equals(Profile.EMAIL)) {
			person.setEmail(argument);
		} else if (command.equals(Profile.CALORIES_MEAL)) {
			try {
				Long cal = Long.parseLong(argument);
				person.setCaloriesMeal(cal);
			} catch (NumberFormatException e) {
				Action.sendKeyboard(bot, chatId, "Sorry, not a valid number<b>\n\nTry again!</b>");
				return;
			}
		}
		Response res = BotClient.getService().path("person/" + chatId).request().accept(MediaType.APPLICATION_XML).put(Entity.xml(person));

		String firstPart = null;
		if (res.getStatus() == 200) {
			firstPart = "Ok, your " + command.substring(1) + " is " + argument + "\n\n<b>Well done!</b>";
		} else {
			firstPart = Action.ERROR;
		}
		Action.sendKeyboard(bot, chatId, firstPart);
	}

	protected static void setFirstname (LifeCoachBot bot, Long chatId, String argument) {
		setName(bot, chatId, argument, Profile.FIRSTNAME);
	}

	protected static void setLastname (LifeCoachBot bot, Long chatId, String argument) {
		setName(bot, chatId, argument, Profile.LASTNAME);
	}

	protected static void setBirthDate (LifeCoachBot bot, Long chatId, String argument) {
		setName(bot, chatId, argument, Profile.BIRTHDAY);
	}

	protected static void setEmail (LifeCoachBot bot, Long chatId, String argument) {
		setName(bot, chatId, argument, Profile.EMAIL);
	}

	protected static void setCaloriesMeal(LifeCoachBot bot, Long chatId, String argument) {
		setName(bot, chatId, argument, Profile.CALORIES_MEAL);
	}

	public static void getProfile (LifeCoachBot bot, Long chatId) {
		System.out.println("Seeing profile");
		Response res = BotClient.getService().path("person/" + chatId).request().accept(MediaType.APPLICATION_XML).get();

		String firstPart = null;
		if (res.getStatus() == 200) {
			PersonModel person = res.readEntity(PersonModel.class);
			
			firstPart = "<b>Your profile</b>\nFirstname: <i>" + person.getFirstname() + "</i>\n";
			if (person.getLastname() != null) { firstPart += "Lastname: <i>" + person.getLastname() + "</i>\n"; }
			if (person.getBirthdate() != null) { firstPart += "Birthday: <i>" + person.getBirthdate() + "</i>\n"; }
			if (person.getEmail() != null) { firstPart += "E-mail: <i>" + person.getEmail() + "</i>\n"; }
			if (person.getCaloriesMeal() != null) { firstPart += "Kcal per meal: <i>" + person.getCaloriesMeal() + " kcal</i>\n"; }
			if (person.getCurrentProfile() != null && person.getCurrentProfile().size() > 0) { firstPart += "Current measures:\n"; }
			for (MeasureModel m : person.getCurrentProfile()) {
				firstPart += "     * " + m.getMeasureType() + ": <i>" + m.getMeasureValue() + "</i>";
				if (m.getMeasureType().equals("weight")) {
					firstPart += "<i> kg</i>\n";
				} else if (m.getMeasureType().equals("height")) {
					firstPart += "<i> cm</i>\n";
				}
			}
		} else {
			firstPart = Action.ERROR;
		}
		Action.sendKeyboard(bot, chatId, firstPart);
	}
	
	public static void setSleepTime (LifeCoachBot bot, Long chatId, String argument) {
		
		Response res = BotClient.getService().path("exercise/" + chatId + "/timesleep").request().put(Entity.entity(argument, MediaType.TEXT_PLAIN));

		String firstPart = null;
		if (res.getStatus() == 200) {
			firstPart = "Ok, your " + SLEEPING_TIME.substring(1) + " is " + argument + "\n\n<b>Well done!</b>";
		} else {
			firstPart = Action.ERROR;
			System.out.println(res.getStatus());
		}
		Action.sendKeyboard(bot, chatId, firstPart);
	}

}
