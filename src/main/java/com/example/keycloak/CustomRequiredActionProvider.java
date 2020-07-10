package com.example.keycloak;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.resetcred.AbstractSetRequiredActionAuthenticator;

public class CustomRequiredActionProvider extends AbstractSetRequiredActionAuthenticator {
  @Override
  public void authenticate(AuthenticationFlowContext authenticationFlowContext) {

  }

  @Override
  public String getDisplayType() {
    return null;
  }

  @Override
  public String getHelpText() {
    return null;
  }

  @Override
  public String getId() {
    return null;
  }
}
