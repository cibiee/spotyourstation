package com.Ciby.spotyourstation.twitter;


public class Constants {

	public static final String CONSUMER_KEY = "SHQczQ7Z6jF8aOlbszgtcw";
	public static final String CONSUMER_SECRET= "G04TSYjMO6H8tzGAOgfHNHpDbpzsAXW17SO2HI2n4";
	
	public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
	
	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow-twitter";
	public static final String	OAUTH_CALLBACK_HOST		= "callback";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	 
}

