package repository;

import exception.CurrencyAlreadyExistsException;
import exception.DatabaseException;
import mapper.CurrencyMapper;
import model.Currency;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepository {
    private final DataSource dataSource;
    private final CurrencyMapper currencyMapper;

    public CurrencyRepository(DataSource dataSource, CurrencyMapper currencyMapper) {
        this.dataSource = dataSource;
        this.currencyMapper = currencyMapper;
    }

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT * FROM CurrencyExchanger.Currencies";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Currency currency = createCurrency(rs);
                currencies.add(currency);
            }
        }catch(SQLException e){
            throw new DatabaseException("Ошибка запроса к базе данных", e);
        }
        return currencies;
    }

    public Currency save(Currency currency) {
        String sql = "INSERT INTO CurrencyExchanger.Currencies (code, fullName, sign) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, currency.getCode());
            stmt.setString(2, currency.getFullName());
            stmt.setString(3, currency.getSign());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getLong(1));
            }
            return currency;

        } catch (SQLException e) {
            if(e.getSQLState() != null && e.getSQLState().equals("23505")){
                throw new CurrencyAlreadyExistsException("Такая валюта уже существует", e);
            }
            else{
                throw new DatabaseException("Ошибка запроса к базе данных", e);
            }
        }
    }

    public Optional<Currency> findByCode(String code){
        String sql = "Select * FROM CurrencyExchanger.Currencies WHERE code=?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                Currency currency = createCurrency(rs);
                return Optional.of(currency);
            }
            return Optional.empty();

        }catch(SQLException e){
            throw new DatabaseException("Ошибка в бд поиска валюты по коду: " + e.getMessage(), e);
        }
    }

    private Currency createCurrency(ResultSet rs) throws SQLException{
        Currency currency = new Currency();
        currency.setId(rs.getLong("id"));
        currency.setCode(rs.getString("code"));
        currency.setFullName(rs.getString("fullName"));
        currency.setSign(rs.getString("sign"));
        return currency;
    }
}