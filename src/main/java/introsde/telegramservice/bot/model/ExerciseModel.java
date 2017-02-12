package introsde.telegramservice.bot.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="exercise")
public class ExerciseModel {
	
	private String name;
	private Integer id;
	private Integer minutes;
	private Double calories;
	
	public ExerciseModel() {	
	}
	
	public ExerciseModel(String name, Integer minutes) {
		this.name = name;
		this.minutes = minutes;
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

	public Integer getMinutes() {
		return minutes;
	}

	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}

	public Double getCalories() {
		return calories;
	}

	public void setCalories(Double calories) {
		this.calories = calories;
	}


}
