package mapper;

import model.Currency;
import model.ExchangeRate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetMapper {

    public Currency mapToCurrency(ResultSet rs) throws SQLException {
        Currency currency = new Currency();
        currency.setId(rs.getLong("id"));
        currency.setCode(rs.getString("code"));
        currency.setFullName(rs.getString("fullName"));
        currency.setSign(rs.getString("sign"));
        return currency;
    }
    public Currency mapToCurrency(ResultSet rs, String prefix) throws SQLException {
        Currency currency = new Currency();
        currency.setId(rs.getLong(prefix + "_id"));
        currency.setCode(rs.getString(prefix + "_code"));
        currency.setFullName(rs.getString(prefix + "_fullName"));
        currency.setSign(rs.getString(prefix + "_sign"));
        return currency;
    }

    public ExchangeRate mapToExchangeRate(ResultSet rs) throws SQLException {
        Currency baseCurrency = mapToCurrency(rs, "base");
        Currency targetCurrency = mapToCurrency(rs, "target");

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setId(rs.getLong("exchange_rate_id"));
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(rs.getBigDecimal("rate"));

        return exchangeRate;
    }
}