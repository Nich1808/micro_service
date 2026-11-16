-- liquibase formatted sql

-- changeset istad:004-create-registered-client
CREATE TABLE oauth2_registered_client
(
    id                            VARCHAR(100)  NOT NULL,
    client_id                     VARCHAR(100)  NOT NULL,
    client_id_issued_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 VARCHAR(200)  DEFAULT NULL,
    client_secret_expires_at      TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    client_name                   VARCHAR(200)  NOT NULL,
    client_authentication_methods VARCHAR(1000) NOT NULL,
    authorization_grant_types     VARCHAR(1000) NOT NULL,
    redirect_uris                 VARCHAR(1000) DEFAULT NULL,
    post_logout_redirect_uris     VARCHAR(1000) DEFAULT NULL,
    scopes                        VARCHAR(1000) NOT NULL,
    client_settings               VARCHAR(2000) NOT NULL,
    token_settings                VARCHAR(2000) NOT NULL,
    CONSTRAINT pk_oauth2_registered_client PRIMARY KEY (id),
    CONSTRAINT uk_oauth2_registered_client_id UNIQUE (client_id)
);

-- changeset istad:004-create-authorization
CREATE TABLE oauth2_authorization
(
    id                            VARCHAR(100) NOT NULL,
    registered_client_id          VARCHAR(100) NOT NULL,
    principal_name                VARCHAR(200) NOT NULL,
    authorization_grant_type      VARCHAR(100) NOT NULL,
    authorized_scopes             VARCHAR(1000) DEFAULT NULL,
    attributes                    TEXT DEFAULT NULL,
    state                         VARCHAR(500) DEFAULT NULL,
    authorization_code_value      TEXT DEFAULT NULL,
    authorization_code_issued_at  TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    authorization_code_expires_at TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    authorization_code_metadata   TEXT DEFAULT NULL,
    access_token_value            TEXT DEFAULT NULL,
    access_token_issued_at        TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    access_token_expires_at       TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    access_token_metadata         TEXT DEFAULT NULL,
    access_token_type             VARCHAR(100) DEFAULT NULL,
    access_token_scopes           VARCHAR(1000) DEFAULT NULL,
    oidc_id_token_value           TEXT DEFAULT NULL,
    oidc_id_token_issued_at       TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    oidc_id_token_expires_at      TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    oidc_id_token_metadata        TEXT DEFAULT NULL,
    refresh_token_value           TEXT DEFAULT NULL,
    refresh_token_issued_at       TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    refresh_token_expires_at      TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    refresh_token_metadata        TEXT DEFAULT NULL,
    user_code_value               TEXT DEFAULT NULL,
    user_code_issued_at           TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    user_code_expires_at          TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    user_code_metadata            TEXT DEFAULT NULL,
    device_code_value             TEXT DEFAULT NULL,
    device_code_issued_at         TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    device_code_expires_at        TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    device_code_metadata          TEXT DEFAULT NULL,
    CONSTRAINT pk_oauth2_authorization PRIMARY KEY (id)
);

-- changeset istad:004-index-authorization
CREATE INDEX idx_oauth2_authorization_client ON oauth2_authorization (registered_client_id);
CREATE INDEX idx_oauth2_authorization_principal ON oauth2_authorization (principal_name);
CREATE INDEX idx_oauth2_authorization_state ON oauth2_authorization (state);

-- changeset istad:004-create-authorization-consent
CREATE TABLE oauth2_authorization_consent
(
    registered_client_id VARCHAR(100)  NOT NULL,
    principal_name       VARCHAR(200)  NOT NULL,
    authorities          VARCHAR(1000) NOT NULL,
    CONSTRAINT pk_oauth2_authorization_consent
        PRIMARY KEY (registered_client_id, principal_name)
);
