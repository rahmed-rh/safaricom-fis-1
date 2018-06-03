package com.rahmed.redhat.demo.rest;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements CallbackHandler {
	final static Logger LOG = LoggerFactory.getLogger(LoginHandler.class);
	final private String username;
	final private char[] password;
	final private String realm;

	public LoginHandler(String username, char[] password, String realm) {
		this.username = username;
		this.password = password;
		this.realm = realm;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		LOG.info("Entering handle username="+username+" , password="+password.toString()+" ,realm="+realm);
		for (Callback callback : callbacks) {
			if (callback instanceof NameCallback) {
				LOG.info("Entering instanceof NameCallback");
				NameCallback nameCallback = (NameCallback) callback;
				nameCallback.setName(username);
			} else if (callback instanceof PasswordCallback) {
				LOG.info("Entering instanceof PasswordCallback");
				PasswordCallback passwordCallback = (PasswordCallback) callback;
				passwordCallback.setPassword(password);
			} else if (callback instanceof AuthorizeCallback) {
				LOG.info("Entering instanceof AuthorizeCallback");
				AuthorizeCallback authorizeCallback = (AuthorizeCallback) callback;
				authorizeCallback.setAuthorized(
						authorizeCallback.getAuthenticationID().equals(authorizeCallback.getAuthorizationID()));
			} else if (callback instanceof RealmCallback) {
				LOG.info("Entering instanceof RealmCallback");
				RealmCallback realmCallback = (RealmCallback) callback;
				realmCallback.setText(realm);
			} else {
				LOG.info("Entering UnsupportedCallbackException");
				throw new UnsupportedCallbackException(callback);
			}
		}
	}

}
