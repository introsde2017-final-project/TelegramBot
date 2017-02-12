package introsde.telegramservice.bot.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="person")
public class PersonModel {

	private String firstname;
	private String lastname;
    protected Long chatId;
    protected String birthdate;
    protected String email;
    private Long caloriesMeal;
	private List<MeasureModel> currentProfile;
	
	public PersonModel() {
	}
	
	public PersonModel(String firstname, String lastname, Long chatId) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.chatId = chatId;
	}
	
	public String getFirstname() {
		return firstname;
	}
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public Long getChatId() {
		return chatId;
	}
	
	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getCaloriesMeal() {
		return caloriesMeal;
	}

	public void setCaloriesMeal(Long caloriesMeal) {
		this.caloriesMeal = caloriesMeal;
	}

	@XmlElementWrapper(name = "currentHealth")
	@XmlElement(name = "measure")
	public List<MeasureModel> getCurrentProfile() {
		return currentProfile;
	}

	public void setCurrentProfile(List<MeasureModel> currentProfile) {
		this.currentProfile = currentProfile;
	}
}
