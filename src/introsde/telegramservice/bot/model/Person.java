package introsde.telegramservice.bot.model;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Person {

	private String firstname;
	private String lastname;
	private Long chatId;
	
	public Person() {
		
	}
	
	public Person(String firstname, String lastname, Long chatId) {
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
}
