package com.agitive.usembassy.databases;

public class FAQItem {

	private String question;
	private String answer;
	
	public FAQItem() {
		this.question = "";
		this.answer = "";
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	public String getQuestion() {
		return this.question;
	}
	
	public String getAnswer() {
		return this.answer;
	}
}
