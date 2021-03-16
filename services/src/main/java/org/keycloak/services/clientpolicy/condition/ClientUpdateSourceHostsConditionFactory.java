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

import java.util.List;

import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ConfigurationValidationHelper;
import org.keycloak.provider.ProviderConfigProperty;

public class ClientUpdateSourceHostsConditionFactory extends AbstractClientPolicyConditionProviderFactory {

    public static final String PROVIDER_ID = "clientupdatesourcehost-condition";

    public static final String TRUSTED_HOSTS = "trusted-hosts";

    public static final String HOST_SENDING_REQUEST_MUST_MATCH = "host-sending-request-must-match";

    private static final ProviderConfigProperty TRUSTED_HOSTS_PROPERTY = new ProviderConfigProperty(TRUSTED_HOSTS, "clientupdate-trusted-hosts.label", "clientupdate-trusted-hosts.tooltip", ProviderConfigProperty.MULTIVALUED_STRING_TYPE, null);

    private static final ProviderConfigProperty HOST_SENDING_REGISTRATION_REQUEST_MUST_MATCH_PROPERTY = new ProviderConfigProperty(HOST_SENDING_REQUEST_MUST_MATCH, "host-sending-request-must-match.label",
            "host-sending-request-must-match.tooltip", ProviderConfigProperty.BOOLEAN_TYPE, "true");

    @Override
    public ClientPolicyConditionProvider create(KeycloakSession session, ComponentModel model) {
        return new ClientUpdateSourceHostsCondition(session, model);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "The condition checks the host/domain of the entity who tries to create/update the client to determine whether the policy is applied.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> l = super.getConfigProperties();
        l.add(TRUSTED_HOSTS_PROPERTY);
        l.add(HOST_SENDING_REGISTRATION_REQUEST_MUST_MATCH_PROPERTY);
        return l;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        ConfigurationValidationHelper.check(config)
                .checkBoolean(HOST_SENDING_REGISTRATION_REQUEST_MUST_MATCH_PROPERTY, true);

        ClientUpdateSourceHostsCondition policy = new ClientUpdateSourceHostsCondition(session, config);
        if (!policy.isHostMustMatch()) {
            throw new ComponentValidationException("At least one of hosts verification must be enabled");
        }

    }
}
