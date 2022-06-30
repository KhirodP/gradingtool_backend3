package com.pwc.grading.project.model;
/**
 * A class having all the details of survey data
 *
 */
public class Survey {

	private String surveyId;
	private long time;
	private long lastUpdate;
	private int maxScore;
	private String surveyData;
	private boolean publish;

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	public Survey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}

	public String getSurveyData() {
		return surveyData;
	}

	public void setSurveyData(String surveyData) {
		this.surveyData = surveyData;
	}

	@Override
	public String toString() {
		return "Survey [surveyId=" + surveyId + ", time=" + time + ", lastUpdate=" + lastUpdate + ", maxScore="
				+ maxScore + ", surveyData=" + surveyData + "]";
	}

}
