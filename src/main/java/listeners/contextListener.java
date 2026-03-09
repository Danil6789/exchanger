//package listeners;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import jakarta.servlet.ServletContextEvent;
//import jakarta.servlet.ServletContextListener;
//import jakarta.servlet.annotation.WebListener;
//import repositories.CurrencyRepository;
//import repositories.ExchangeRateRepository;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.Properties;
//
//@WebListener
//public class contextListener implements ServletContextListener {
//
//    @Override
//    public void contextInitialized(ServletContextEvent sce) {
//        try {
//            System.out.println("🚀 Инициализация приложения...");
//
//            // 1. Загружаем свойства
//            Properties props = new Properties();
//            try (InputStream input = sce.getServletContext()
//                    .getResourceAsStream("/WEB-INF/properties/db.properties")) {
//
//                // ВАЖНО: проверяем, что файл найден!
//                if (input == null) {
//                    System.err.println("❌ Файл /WEB-INF/properties/db.properties НЕ НАЙДЕН!");
//                    System.err.println("Real path: " + sce.getServletContext().getRealPath("/WEB-INF/properties/db.properties"));
//                    throw new RuntimeException("Файл свойств не найден");
//                }
//
//                System.out.println("✅ Файл свойств загружен");
//                props.load(input);
//
//                // Проверяем, что все необходимые свойства есть
//                checkRequiredProperties(props);
//
//            } catch (IOException e) {
//                throw new RuntimeException("Ошибка чтения файла свойств", e);
//            }
//
//            // 2. Создаем пул соединений
//            System.out.println("🔄 Создание пула соединений...");
//            HikariDataSource dataSource = createHikariPool(props);
//            System.out.println("✅ Пул соединений создан");
//
//            // 3. Проверяем реальное подключение
//            try (Connection conn = dataSource.getConnection()) {
//                System.out.println("✅ Реальное подключение к БД успешно!");
//            } catch (SQLException e) {
//                System.err.println("❌ Не удалось подключиться к БД: " + e.getMessage());
//                throw new RuntimeException("Ошибка подключения к БД", e);
//            }
//
//            // 4. Сохраняем DataSource в контексте
//            sce.getServletContext().setAttribute("dataSource", dataSource);
//
//            // 5. Создаем репозитории
//            System.out.println("🔄 Создание репозиториев...");
//            CurrencyRepository currencyRepo = new CurrencyRepository(dataSource);
//            ExchangeRateRepository exchangeRateRepo = new ExchangeRateRepository(dataSource);
//
//            sce.getServletContext().setAttribute("currencyRepo", currencyRepo);
//            sce.getServletContext().setAttribute("exchangeRateRepo", exchangeRateRepo);
//
//            System.out.println("🎉 Инициализация успешно завершена!");
//
//        } catch (Exception e) {
//            System.err.println("❌ КРИТИЧЕСКАЯ ОШИБКА:");
//            e.printStackTrace();  // ЭТО ОЧЕНЬ ВАЖНО!
//            throw new RuntimeException("Ошибка инициализации приложения", e);
//        }
//    }
//
//    private void checkRequiredProperties(Properties props) {
//        String[] required = {"db.url", "db.username", "db.password", "db.driver.name"};
//        for (String prop : required) {
//            if (props.getProperty(prop) == null) {
//                throw new RuntimeException("Отсутствует обязательное свойство: " + prop);
//            }
//        }
//    }
//
//    public HikariDataSource createHikariPool(Properties props) {
//        HikariConfig config = new HikariConfig();
//
//        String url;
//        String dbHost = System.getenv("DB_HOST");
//
//        if (dbHost != null) {
//            // Для Docker
//            String dbPort = System.getenv().getOrDefault("DB_PORT", "5432");
//            String dbName = System.getenv().getOrDefault("DB_NAME", "exchanger_db");
//            url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;
//            System.out.println("🔵 Docker режим: подключение к " + url);
//        } else {
//            url = props.getProperty("db.url");
//            if (url == null) {
//                throw new RuntimeException("db.url не найден в свойствах");
//            }
//            System.out.println("🟢 Локальный режим: подключение к " + url);
//        }
//        config.setJdbcUrl(url);
//
//        // Определяем username
//        String username = System.getenv("DB_USER");
//        if (username == null) {
//            username = props.getProperty("db.username");
//            if (username == null) {
//                throw new RuntimeException("db.username не найден");
//            }
//        }
//        config.setUsername(username);
//
//        // Определяем password
//        String password = System.getenv("DB_PASSWORD");
//        if (password == null) {
//            password = props.getProperty("db.password");
//            if (password == null) {
//                throw new RuntimeException("db.password не найден");
//            }
//        }
//        config.setPassword(password);
//
//        // Driver class
//        String driver = props.getProperty("db.driver.name");
//        if (driver == null) {
//            throw new RuntimeException("db.driver.name не найден");
//        }
//        config.setDriverClassName(driver);
//
//        // Дополнительные настройки
//        config.setMaximumPoolSize(10);
//        config.setMinimumIdle(2);
//        config.setConnectionTimeout(30000);
//        config.setIdleTimeout(600000);
//        config.setMaxLifetime(1800000);
//
//        return new HikariDataSource(config);
//    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent sce) {
//        System.out.println("🚀 Уничтожение приложения...");
//        DataSource ds = (DataSource) sce.getServletContext().getAttribute("dataSource");
//        if (ds instanceof HikariDataSource) {
//            ((HikariDataSource) ds).close();
//            System.out.println("✅ Пул соединений закрыт");
//        }
//    }
//}




package listeners;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import repositories.CurrencyRepository;
import repositories.ExchangeRateRepository;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;


@WebListener
public class contextListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce){
        try{
            Properties props = new Properties();
            try (InputStream input = sce.getServletContext()
                    .getResourceAsStream("/WEB-INF/properties/db.properties")) {
                props.load(input);
            }
            HikariDataSource dataSource = createHikariPool(props);
            sce.getServletContext().setAttribute("dataSource", dataSource);

            CurrencyRepository currencyRepo = new CurrencyRepository(dataSource);
            ExchangeRateRepository exchangeRateRepo = new ExchangeRateRepository(dataSource);

            sce.getServletContext().setAttribute("currencyRepo", currencyRepo);
            sce.getServletContext().setAttribute("exchangeRateRepo", exchangeRateRepo);
        }
        catch(Exception e){
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

