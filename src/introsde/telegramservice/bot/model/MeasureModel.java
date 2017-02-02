package introsde.telegramservice.bot.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MeasureModel {
	private String measureValue;
	private String measureType;
	private String measureValueType;
	
	public MeasureModel() {
		
	}
	
	public MeasureModel (String measureValue, String measureType, String measureValueType) {
		this.measureValue = measureValue;
		this.measureType = measureType;
		this.measureValueType = measureValueType;
	}
	
	public String getMeasureValue() {
		return measureValue;
	}
	public void setMeasureValue(String measureValue) {
		this.measureValue = measureValue;
	}
	public String getMeasureType() {
		return measureType;
	}
	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}
	public String getMeasureValueType() {
		return measureValueType;
	}
	public void setMeasureValueType(String measureValueType) {
		this.measureValueType = measureValueType;
	}
}
