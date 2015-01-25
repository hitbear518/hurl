package me.sw.hurl;

/**
 * Created by Sen on 1/25/2015.
 */
public class QuestionLoadedEvent {
	final SOQuestions questions;

	public QuestionLoadedEvent(SOQuestions questions) {
		this.questions = questions;
	}
}
