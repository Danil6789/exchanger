package repositories;

import models.Currency;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepository {
    private final DataSource dataSource;

    public CurrencyRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Currency> findAll() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT * FROM CurrencyExchanger.Currencies";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Currency c = new Currency();
                c.setId(rs.getLong("id"));
                c.setCode(rs.getString("code"));
                c.setName(rs.getString("fullName"));
                c.setSign(rs.getString("sign"));
                currencies.add(c);
            }
        }
        return currencies;
    }

    public Currency save(Currency currency) {
        String sql = "INSERT INTO CurrencyExchanger.Currencies (code, fullName, sign) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getName());
            stmt.setString(3, currency.getSign());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getLong(1));
            }

            return currency;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения валюты", e);
        }
    }

    public Optional<Currency> findByCode(String code){
        String sql = "Select * FROM CurrencyExchanger.Currencies WHERE code=?";
        try(Connection conn = dataSource.getConnection()){

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Currency currency = new Currency();
                currency.setId(rs.getLong("id"));
                currency.setCode(rs.getString("code"));
                currency.setName(rs.getString("fullName"));
                currency.setSign(rs.getString("sign"));
                return Optional.of(currency);
            }
            return Optional.empty();

        }catch(SQLException e){
            throw new RuntimeException("Ошибка поиска валюты по коду: " + e.getMessage(), e);
        }
    }
}
