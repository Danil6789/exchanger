package repositories;

import models.Currency;
import models.ExchangeRate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class ExchangeRateRepository {
    private final DataSource dataSource;

    public ExchangeRateRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public ExchangeRate save(ExchangeRate exchangeRate) {
        String sql = "INSERT INTO CurrencyExchanger.ExchangeRates (baseCurrencyId, targetCurrencyId, rate) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, exchangeRate.getBaseCurrency().getId());

            stmt.setLong(2, exchangeRate.getTargetCurrency().getId());

            stmt.setBigDecimal(3, exchangeRate.getRate());

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getLong(1));
            }

            return exchangeRate;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения курса валют", e);
        }
    }

    public List<ExchangeRate> findAll() throws SQLException {
        List<ExchangeRate> rates = new ArrayList<>();
        String sql = """
                SELECT  
                er.id as exchange_rate_id,
                bc.id as base_id,
                bc.fullName as base_fullName,
                bc.code as base_code,
                bc.sign as base_sign,
                tc.id as target_id,
                tc.fullName as target_fullName,
                tc.code as target_code,
                tc.sign as target_sign,
                er.rate
                FROM CurrencyExchanger.ExchangeRates as er 
                JOIN CurrencyExchanger.Currencies as bc ON bc.id=er.baseCurrencyId
                JOIN CurrencyExchanger.Currencies as tc ON tc.id=er.targetCurrencyId""";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Currency baseCurrency = new Currency();
                baseCurrency.setId(rs.getLong("base_id"));
                baseCurrency.setCode(rs.getString("base_code"));
                baseCurrency.setFullName(rs.getString("base_fullName"));
                baseCurrency.setSign(rs.getString("base_sign"));

                Currency targetCurrency = new Currency();
                targetCurrency.setId(rs.getLong("target_id"));
                targetCurrency.setCode(rs.getString("target_code"));
                targetCurrency.setFullName(rs.getString("target_fullName"));
                targetCurrency.setSign(rs.getString("target_sign"));

                ExchangeRate rate = new ExchangeRate();
                rate.setId(rs.getLong("exchange_rate_id"));
                rate.setBaseCurrency(baseCurrency);
                rate.setTargetCurrency(targetCurrency);
                rate.setRate(rs.getBigDecimal("rate"));

                rates.add(rate);
            }
        }
        return rates;
    }

    public Optional<ExchangeRate> findByCoupleCodes(String baseCurrencyCode, String targetCurrencyCode){
        String sql = """ 
            SELECT
                er.id as exchange_rate_id,
                bc.id as base_id,
                bc.fullName as base_fullName,
                bc.code as base_code,
                bc.sign as base_sign,
                tc.id as target_id,
                tc.fullName as target_fullName,
                tc.code as target_code,
                tc.sign as target_sign,
                er.rate
            FROM CurrencyExchanger.ExchangeRates as er
            JOIN CurrencyExchanger.Currencies as bc ON bc.id = er.baseCurrencyId
            JOIN CurrencyExchanger.Currencies as tc ON tc.id = er.targetCurrencyId
            WHERE bc.code = ? AND tc.code = ?
            """;

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                Currency baseCurrency = new Currency();
                baseCurrency.setId(rs.getLong("base_id"));
                baseCurrency.setCode(rs.getString("base_code"));
                baseCurrency.setFullName(rs.getString("base_fullName"));
                baseCurrency.setSign(rs.getString("base_sign"));

                Currency targetCurrency = new Currency();
                targetCurrency.setId(rs.getLong("target_id"));
                targetCurrency.setCode(rs.getString("target_code"));
                targetCurrency.setFullName(rs.getString("target_fullName"));
                targetCurrency.setSign(rs.getString("target_sign"));

                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setId(rs.getLong("exchange_rate_id"));
                exchangeRate.setBaseCurrency(baseCurrency);
                exchangeRate.setTargetCurrency(targetCurrency);
                exchangeRate.setRate(rs.getBigDecimal("rate"));

                return Optional.of(exchangeRate);
            }
            return Optional.empty();

        } catch (SQLException e){
            throw new RuntimeException("Ошибка поиска пары кодов в обменнике: " + e.getMessage(), e);
        }
    }

    public ExchangeRate updateRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate){
        String sql = """
            UPDATE CurrencyExchanger.ExchangeRates 
            SET rate = ?
            WHERE baseCurrencyId = (SELECT id FROM CurrencyExchanger.Currencies WHERE code = ?)
              AND targetCurrencyId = (SELECT id FROM CurrencyExchanger.Currencies WHERE code = ?)
        """;

        try(Connection conn = dataSource.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBigDecimal(1, rate);
            stmt.setString(2, baseCurrencyCode);
            stmt.setString(3, targetCurrencyCode);
            int checked = stmt.executeUpdate();

            if (checked == 0) {
                throw new RuntimeException("В обменнике не было такой пары: "
                        + baseCurrencyCode + targetCurrencyCode);
            }
            return findByCoupleCodes(baseCurrencyCode, targetCurrencyCode).get();

        }catch(SQLException e){
            throw new RuntimeException("Ошибка изменения ставки(rate): " + e.getMessage(), e);
        }

    }
}