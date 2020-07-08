package com.example.keycloak.utils;

import java.util.Optional;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;

public class UserUtils {

  private static final String ATTR_EMAIL_KEY = "EMAIL";

  public static UserModel findUserByNameOrEmailOrSecondaryEmail(KeycloakSession session, RealmModel realm, String username) {
    UserModel userModel;
    userModel = KeycloakModelUtils.findUserByNameOrEmail(session, realm, username);

    if(userModel == null) {
      Optional<UserModel> userFromSecondaryEmail = findUserBySecondaryEmail(session, realm, username);
      if(userFromSecondaryEmail.isPresent()){
        return userFromSecondaryEmail.get();
      }
    }
    return userModel;
  }

  public static Optional<UserModel> findUserBySecondaryEmail(KeycloakSession session, RealmModel realm, String email) {
    return session
        .users()
        .searchForUserByUserAttribute(ATTR_EMAIL_KEY, email, realm)
        .stream().findFirst();
  }
}
