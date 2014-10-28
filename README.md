Tweetranslate
===============
Translate your tweet to another language before sending it.

Edit Twitter Key/Secret
-------------------------
* Go to https://apps.twitter.com/app/ and create a new app (Make sure you enter a Callback URL, it can be anything)
* Once the app is created go to Permissions tab under your app and make sure "Read, Write and Access direct messages" is ticked
* Now go into "Keys and Access Tokens", make note of the Consumer Key and Consumer Secret.
* Now go to Secrets.java and edit the variables with the Consumer Key and Secret you obtained earlier:
```java
public static final String CONSUMER_KEY = "";
public static final String CONSUMER_SECRET = "";
```
* Next step is to open the strings.xml and edit the app_id to your facebook app_id:
```xml
<string name="app_id"></string>
```
