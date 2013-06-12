package com.Ciby.spotyourstation.twitter;

import android.content.SharedPreferences;
import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;

public class TwitterUtils {
//a30160282f39c9b610469e203f142069
	public static boolean isAuthenticated(SharedPreferences prefs) {

		String token = prefs.getString(OAuth.OAUTH_TOKEN, "1328897070-fBAqj7ABCE83IkGARzuNZA9GArkssQ4xZAZu7jp");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "WGYs0HELcdhr3alt4GF4OU8vNhr0YyvajbneWnrGIII");
		try {
		AccessToken a = new AccessToken(token,secret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		
		 
			twitter.getAccountSettings();
			return true;
		} catch (TwitterException e) {
			return false;
		}
	}
	
	public static void sendTweet(SharedPreferences prefs,String msg) throws Exception {
		try {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "1328897070-fBAqj7ABCE83IkGARzuNZA9GArkssQ4xZAZu7jp");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "WGYs0HELcdhr3alt4GF4OU8vNhr0YyvajbneWnrGIII");
		
		AccessToken a = new AccessToken(token,secret);
		Twitter twitter = new TwitterFactory().getInstance();
		
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
        twitter.updateStatus(msg);
         
        
		} catch (TwitterException e) {
			 String st = e.toString();
			 String m = "";
		}
	}	
}
