package com.example.keycloak;

import com.example.keycloak.utils.UserUtils;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.UserModel;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.messages.Messages;

public class DemoWDGUserPasswordFormProvider extends UsernamePasswordForm {


// override methods below


  @Override
  public boolean validateUserAndPassword(AuthenticationFlowContext context, MultivaluedMap<String, String> inputData)  {
    context.clearUser();
    UserModel user = getUser(context, inputData);
    return user != null && validatePassword(context, user, inputData) && validateUser(context, user, inputData);
  }

  @Override
  public boolean validateUser(AuthenticationFlowContext context, MultivaluedMap<String, String> inputData) {
    context.clearUser();
    UserModel user = getUser(context, inputData);
    return user != null && validateUser(context, user, inputData);
  }

  private UserModel getUser(AuthenticationFlowContext context, MultivaluedMap<String, String> inputData) {
    String username = inputData.getFirst(AuthenticationManager.FORM_USERNAME);


    if (username == null) {
      context.getEvent().error(Errors.USER_NOT_FOUND);
      Response challengeResponse = challenge(context, getDefaultChallengeMessage(context));
      context.failureChallenge(AuthenticationFlowError.INVALID_USER, challengeResponse);
      return null;
    }

    // remove leading and trailing whitespace
    username = username.trim();

    context.getEvent().detail(Details.USERNAME, username);
    context.getAuthenticationSession().setAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME, username);

    UserModel user = null;
    try {
      // user = KeycloakModelUtils.findUserByNameOrEmail(context.getSession(), context.getRealm(), username);
      user = UserUtils.findUserByNameOrEmailOrSecondaryEmail(context.getSession(), context.getRealm(), username);
    } catch (ModelDuplicateException mde) {
      ServicesLogger.LOGGER.modelDuplicateException(mde);

      // Could happen during federation import
      if (mde.getDuplicateFieldName() != null && mde.getDuplicateFieldName().equals(UserModel.EMAIL)) {
        setDuplicateUserChallenge(context, Errors.EMAIL_IN_USE, Messages.EMAIL_EXISTS, AuthenticationFlowError.INVALID_USER);
      } else {
        setDuplicateUserChallenge(context, Errors.USERNAME_IN_USE, Messages.USERNAME_EXISTS, AuthenticationFlowError.INVALID_USER);
      }
      return user;
    }

    testInvalidUser(context, user);
    return user;
  }

  private boolean validateUser(AuthenticationFlowContext context, UserModel user, MultivaluedMap<String, String> inputData) {
    if (!enabledUser(context, user)) {
      return false;
    }
    String rememberMe = inputData.getFirst("rememberMe");
    boolean remember = rememberMe != null && rememberMe.equalsIgnoreCase("on");
    if (remember) {
      context.getAuthenticationSession().setAuthNote(Details.REMEMBER_ME, "true");
      context.getEvent().detail(Details.REMEMBER_ME, "true");
    } else {
      context.getAuthenticationSession().removeAuthNote(Details.REMEMBER_ME);
    }
    context.setUser(user);
    return true;
  }



}
