/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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

package org.keycloak.models.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Binding between client and clientScope
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@NamedQueries({
        @NamedQuery(name="clientScopeClientMappingIdsByClient", query="select m.clientScopeId from ClientScopeClientMappingEntity m where m.client = :client and m.defaultScope = :defaultScope"),
        @NamedQuery(name="deleteClientScopeClientMapping", query="delete from ClientScopeClientMappingEntity where client = :client and clientScopeId = :clientScopeId"),
        @NamedQuery(name="deleteClientScopeClientMappingByClient", query="delete from ClientScopeClientMappingEntity where client = :client")
})
@Entity
@Table(name="CLIENT_SCOPE_CLIENT")
@IdClass(ClientScopeClientMappingEntity.Key.class)
public class ClientScopeClientMappingEntity {

    @Id
    @Column(name = "SCOPE_ID")
    protected String clientScopeId;

    @Id
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="CLIENT_ID")
    protected ClientEntity client;

    @Column(name="DEFAULT_SCOPE")
    protected boolean defaultScope;

    public String getClientScopeId() {
        return clientScopeId;
    }

    public void setClientScopeId(String clientScopeId) {
        this.clientScopeId = clientScopeId;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public boolean isDefaultScope() {
        return defaultScope;
    }

    public void setDefaultScope(boolean defaultScope) {
        this.defaultScope = defaultScope;
    }

    public static class Key implements Serializable {

        protected String clientScopeId;

        protected ClientEntity client;

        public Key() {
        }

        public Key(String clientScopeId, ClientEntity client) {
            this.clientScopeId = clientScopeId;
            this.client = client;
        }

        public String getClientScopeId() {
            return clientScopeId;
        }

        public ClientEntity getClient() {
            return client;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClientScopeClientMappingEntity.Key key = (ClientScopeClientMappingEntity.Key) o;

            if (clientScopeId != null ? !clientScopeId.equals(key.getClientScopeId() != null ? key.getClientScopeId() : null) : key.getClientScopeId() != null) return false;
            if (client != null ? !client.getId().equals(key.client != null ? key.client.getId() : null) : key.client != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = clientScopeId != null ? clientScopeId.hashCode() : 0;
            result = 31 * result + (client != null ? client.getId().hashCode() : 0);
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!(o instanceof ClientScopeClientMappingEntity)) return false;

        ClientScopeClientMappingEntity key = (ClientScopeClientMappingEntity) o;

        if (clientScopeId != null ? !clientScopeId.equals(key.getClientScopeId() != null ? key.getClientScopeId() : null) : key.getClientScopeId() != null) return false;
        if (client != null ? !client.getId().equals(key.client != null ? key.client.getId() : null) : key.client != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientScopeId != null ? clientScopeId.hashCode() : 0;
        result = 31 * result + (client != null ? client.getId().hashCode() : 0);
        return result;
    }
}
