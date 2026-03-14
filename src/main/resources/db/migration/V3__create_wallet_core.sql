CREATE TABLE IF NOT EXISTS wallet_balances (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    asset VARCHAR(16) NOT NULL,
    available NUMERIC(36, 18) NOT NULL DEFAULT 0,
    locked NUMERIC(36, 18) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, asset)
);

CREATE TABLE IF NOT EXISTS wallet_ledger_entries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    asset VARCHAR(16) NOT NULL,
    entry_type VARCHAR(32) NOT NULL,
    amount NUMERIC(36, 18) NOT NULL,
    reference_type VARCHAR(32) NOT NULL,
    reference_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS wallet_addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    asset VARCHAR(16) NOT NULL,
    network VARCHAR(32) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, asset, network)
);

CREATE TABLE IF NOT EXISTS wallet_deposits (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    asset VARCHAR(16) NOT NULL,
    network VARCHAR(32) NOT NULL,
    address VARCHAR(255) NOT NULL,
    tx_hash VARCHAR(128),
    amount NUMERIC(36, 18) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS wallet_withdrawals (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    asset VARCHAR(16) NOT NULL,
    network VARCHAR(32) NOT NULL,
    address VARCHAR(255) NOT NULL,
    amount NUMERIC(36, 18) NOT NULL,
    fee NUMERIC(36, 18) NOT NULL DEFAULT 0,
    status VARCHAR(16) NOT NULL,
    client_withdrawal_id VARCHAR(64),
    idempotency_key VARCHAR(128),
    provider_ref VARCHAR(128),
    error_message VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, client_withdrawal_id),
    UNIQUE (user_id, idempotency_key)
);