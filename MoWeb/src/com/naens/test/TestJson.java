package com.naens.test;

import com.google.gson.Gson;
import com.naens.moweb.model.GoogleProfile;

public class TestJson {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String json1 = "{ \"kind\": \"plus#person\", \"etag\": \"\\\"MoxPKeu0NQD8g5Gtts3ebh50504/58PNRkoEoPYBD21pYMz4PMZB5JM\\\"\", "
				+"\"emails\": [ { \"value\": \"andrei.nesterov@gmail.com\", \"type\": \"account\" } ], \"objectType\": \"person\", "
				+"\"id\": \"116061730272696151765\", \"displayName\": \"AN\", \"name\": { \"familyName\": \"\", \"givenName\": \"\" }, "
				+"\"image\": { \"url\": \"https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=50\", "
				+"\"isDefault\": true }, \"isPlusUser\": false, \"circledByCount\": 1, \"verified\": false } ";
		String json2 = "{ \"kind\": \"plus#person\", \"displayName\": \"Andrei Nesterov\", \"name\": {\"givenName\": \"Andrei\", \"familyName\": \"Nesterov\" }, "
				+ "\"language\": \"en\", \"isPlusUser\": true, \"url\": \"https://plus.google.com/116061730272696151765\", \"gender\": \"male\", "
				+ "\"image\": {\"url\": \"https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=50\", \"isDefault\": true}, "
				+ "\"emails\": [{\"type\": \"account\", \"value\": \"andrei.nesterov@gmail.com\"}], \"etag\": \"\\\"MoxPKeu0NQD8g5Gtts3ebh50504/0CnREJd4bU4DlmTtXdjFsj9mZvQ\\\"\", "
				+ "\"ageRange\": {\"min\": 21}, \"verified\": false, \"circledByCount\": 1, \"id\": \"116061730272696151765\", \"objectType\": \"person\" }";
		Gson gson = new Gson();
		GoogleProfile person = gson.fromJson(json2, GoogleProfile.class); 
		System.out.println(person);
	}

	/*
{ "kind": "plus#person", "displayName": "Andrei Nesterov", "name": {"givenName": "Andrei", "familyName": "Nesterov" }, 
  "language": "en", "isPlusUser": true, "url": "https://plus.google.com/116061730272696151765", "gender": "male", 
  "image": {"url": "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=50", "isDefault": true}, 
  "emails": [{"type": "account", "value": "andrei.nesterov@gmail.com"}], "etag": "\"MoxPKeu0NQD8g5Gtts3ebh50504/0CnREJd4bU4DlmTtXdjFsj9mZvQ\"", 
  "ageRange": {"min": 21}, "verified": false, "circledByCount": 1, "id": "116061730272696151765", "objectType": "person" }
	 */
}
