package listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import mapper.CurrencyMapper;
import mapper.ExchangeMapper;
import org.mapstruct.factory.Mappers;
import repository.CurrencyRepository;
import repository.ExchangeRateRepository;
import service.ExchangeRateService;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;


@WebListener
public class СontextListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce){
        try{
            System.out.println("1. Начало инициализации");

            Properties props = new Properties();
            System.out.println("2. Загрузка properties");
            try (InputStream input = sce.getServletContext()
                    .getResourceAsStream("/WEB-INF/properties/db.properties")) {

                if (input == null) {
                    System.out.println("3. ФАЙЛ НЕ НАЙДЕН!");
                    throw new RuntimeException("Файл свойств не найден");
                }
                System.out.println("3. Файл найден");
                props.load(input);
                System.out.println("4. Файл загружен");
            }

            System.out.println("5. Создание пула");
            HikariDataSource dataSource = createHikariPool(props);
            System.out.println("6. Пул создан");

            sce.getServletContext().setAttribute("dataSource", dataSource);
            System.out.println("7. DataSource сохранен");

            CurrencyRepository currencyRepo = new CurrencyRepository(dataSource);
            ExchangeRateRepository exchangeRateRepo = new ExchangeRateRepository(dataSource);
            System.out.println("8. Репозитории созданы");

            ExchangeMapper exchangeMapper = Mappers.getMapper(ExchangeMapper.class);
            sce.getServletContext().setAttribute("exchangeMapper", exchangeMapper);

            CurrencyMapper currencyMapper = Mappers.getMapper(CurrencyMapper.class);
            sce.getServletContext().setAttribute("currencyMapper", currencyMapper);

            ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateRepo);
            sce.getServletContext().setAttribute("exchangeRateService", exchangeRateService);

            sce.getServletContext().setAttribute("exchangeRateService", exchangeRateService);

            sce.getServletContext().setAttribute("currencyRepo", currencyRepo);
            sce.getServletContext().setAttribute("exchangeRateRepo", exchangeRateRepo);
            System.out.println("9. Инициализация успешно завершена");

        }
        catch(Exception e){
            System.out.println("❌ Ошибка: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка подключения бд", e);
        }
    }
    public HikariDataSource createHikariPool(Properties props){
        HikariConfig config = new HikariConfig();

        String url;
        String dbHost = System.getenv("DB_HOST");

        if (dbHost != null) {
            String dbPort = System.getenv().getOrDefault("DB_PORT", "5432");
            String dbName = System.getenv().getOrDefault("DB_NAME", "exchanger_db");
            url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        } else {
            url = props.getProperty("db.url");
        }

        config.setJdbcUrl(url);

        String username = System.getenv("DB_USER");
        if (username == null) {
            username = props.getProperty("db.username");
        }
        config.setUsername(username);

        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            password = props.getProperty("db.password");
        }
        config.setPassword(password);

        config.setDriverClassName(props.getProperty("db.driver.name"));

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        return new HikariDataSource(config);
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce){
        DataSource ds = (DataSource) sce.getServletContext().getAttribute("dataSource");
        if (ds instanceof HikariDataSource) {
            ((HikariDataSource) ds).close();
        }
    }
}

