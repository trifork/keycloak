/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.services.clientpolicy.condition;

import java.util.Collections;
import java.util.List;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.services.clientpolicy.ClientPolicyContext;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.clientpolicy.ClientPolicyLogger;
import org.keycloak.services.clientpolicy.ClientPolicyVote;
import org.keycloak.services.clientpolicy.context.ClientCRUDContext;
import org.keycloak.services.clientregistration.ClientRegistrationTokenUtils;
import org.keycloak.util.TokenUtil;

public class ClientUpdateContextCondition extends AbstractClientPolicyConditionProvider {

    private static final Logger logger = Logger.getLogger(ClientUpdateContextCondition.class);

    public ClientUpdateContextCondition(KeycloakSession session, ComponentModel componentModel) {
        super(session, componentModel);
    }

    @Override
    public ClientPolicyVote applyPolicy(ClientPolicyContext context) throws ClientPolicyException {
        switch (context.getEvent()) {
        case REGISTER:
        case UPDATE:
            if (isAuthMethodMatched((ClientCRUDContext)context)) return ClientPolicyVote.YES;
            return ClientPolicyVote.NO;
        default:
            return ClientPolicyVote.ABSTAIN;
        }
    }

    private boolean isAuthMethodMatched(String authMethod) {
        if (authMethod == null) return false;

        List<String> expectedAuthMethods = componentModel.getConfig().get(ClientUpdateContextConditionFactory.UPDATE_CLIENT_SOURCE);
        if (expectedAuthMethods == null) expectedAuthMethods = Collections.emptyList();

        if (logger.isTraceEnabled()) {
            ClientPolicyLogger.log(logger, "auth method = " + authMethod);
            expectedAuthMethods.stream().forEach(i -> ClientPolicyLogger.log(logger, "auth method expected = " + i));
        }

        boolean isMatched = expectedAuthMethods.stream().anyMatch(i -> i.equals(authMethod));
        if (isMatched) {
            ClientPolicyLogger.log(logger, "auth method matched.");
        } else {
            ClientPolicyLogger.log(logger, "auth method unmatched.");
        }
        return isMatched;
    }

    private boolean isAuthMethodMatched(ClientCRUDContext context) {
        String authMethod = null;

        if (context.getToken() == null) {
            authMethod = ClientUpdateContextConditionFactory.BY_ANONYMOUS;
        } else if (isInitialAccessToken(context.getToken())) {
            authMethod = ClientUpdateContextConditionFactory.BY_INITIAL_ACCESS_TOKEN;
        } else if (isRegistrationAccessToken(context.getToken())) {
            authMethod = ClientUpdateContextConditionFactory.BY_REGISTRATION_ACCESS_TOKEN;
        } else if (isBearerToken(context.getToken())) {
            if (context.getAuthenticatedUser() != null || context.getAuthenticatedClient() != null) {
                authMethod = ClientUpdateContextConditionFactory.BY_AUTHENTICATED_USER;
            } else {
                authMethod = ClientUpdateContextConditionFactory.BY_ANONYMOUS;
            }
        }

        return isAuthMethodMatched(authMethod);
    }
 
    private boolean isInitialAccessToken(JsonWebToken jwt) {
        return jwt != null && ClientRegistrationTokenUtils.TYPE_INITIAL_ACCESS_TOKEN.equals(jwt.getType());
    }

    private boolean isRegistrationAccessToken(JsonWebToken jwt) {
        return jwt != null && ClientRegistrationTokenUtils.TYPE_REGISTRATION_ACCESS_TOKEN.equals(jwt.getType());
    }

    private boolean isBearerToken(JsonWebToken jwt) {
        return jwt != null && TokenUtil.TOKEN_TYPE_BEARER.equals(jwt.getType());
    }
}
