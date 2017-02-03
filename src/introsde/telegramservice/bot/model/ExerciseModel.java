package introsde.telegramservice.bot.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="exercise")
public class ExerciseModel {
	
	private String name;
	private Integer id;
	
	public ExerciseModel() {
	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


}
