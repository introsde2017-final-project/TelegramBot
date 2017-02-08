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
	protected static final String CALORIES_MEAL = "/caloriesMeal";
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
				System.out.println("cal to set " + cal);
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
			firstPart = "<b>Your profile</b>\n" +
					"Firstname: " + person.getFirstname() + "\n" +
					"Lastname: " + person.getLastname() + "\n" +
					"Birthday: " + person.getBirthdate() + "\n" +
					"E-mail: " + person.getEmail() + "\n" +
					"Calories per meal: " + person.getCaloriesMeal() + "\n" +
					"Current measures:\n";
			for (MeasureModel m : person.getCurrentProfile()) {
				firstPart += "     " + m.getMeasureType() + ": " + m.getMeasureValue() + "\n";
			}
		} else {
			firstPart = Action.ERROR;
		}
		Action.sendKeyboard(bot, chatId, firstPart);
	}

}
