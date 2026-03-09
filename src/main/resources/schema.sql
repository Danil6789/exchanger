CREATE SCHEMA IF NOT EXISTS CurrencyExchanger;

CREATE TABLE IF NOT EXISTS CurrencyExchanger.Currencies  (
    ID SERIAL PRIMARY KEY NOT NULL,
    Code VARCHAR(10) UNIQUE NOT NULL,
    FullName VARCHAR(100) NOT NULL,
    Sign VARCHAR(3) NOT NULL
);

CREATE TABLE IF NOT EXISTS CurrencyExchanger.ExchangeRates (
    ID SERIAL PRIMARY KEY NOT NULL,
    BaseCurrencyId INTEGER NOT NULL REFERENCES CurrencyExchanger.Currencies (ID),
    TargetCurrencyId INTEGER NOT NULL REFERENCES CurrencyExchanger.Currencies (ID),
    Rate DECIMAL(13,6) NOT NULL,

    CONSTRAINT unique_currencies UNIQUE (BaseCurrencyId, TargetCurrencyId)
);