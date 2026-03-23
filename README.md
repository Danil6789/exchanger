# Currency Exchange API

[![Java](https://img.shields.io/badge/Java-21-red.svg)](https://adoptium.net/)
[![Maven](https://img.shields.io/badge/Maven-3.9-blue.svg)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-✔-blue.svg)](https://www.docker.com/)

## Description

Currency Exchange Project - REST API for managing currencies and exchange rates. Allows you to view and edit lists of currencies and exchange rates, and perform conversion of arbitrary amounts from one currency to another. A web interface is not required for the project.

## Frontend REST API Interface

## Technologies

| Component | Technology |
|-----------|------------|
| **Backend** | Java 21, Jakarta Servlet 6.0, JDBC |
| **Database** | PostgreSQL 15 |
| **Connection Pool** | HikariCP |
| **JSON** | Jackson |
| **Mapping** | MapStruct |
| **Containerization** | Docker, Docker Compose |
| **Web Server** | Tomcat 10.1, Nginx |


## Deployment

```bash
#for docker
apt update && apt upgrade -y
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
#for docker compose
apt install docker-compose-plugin -y

#for project
git clone https://github.com/Danil6789/exchanger.git
cd exchanger
mvn clean package
docker compose up -d
```
## API Endpoints

| Method | URL | Description |
|-------|-----|----------|
| `GET` | `/currencies` | List all currencies |
| `GET` | `/currency/USD` | Get a specific currency |
| `POST` | `/currencies` | Add a new currency |
| `GET` | `/exchangeRates` | List all exchange rates |
| `GET` | `/exchangeRate/USDEUR` | Get rate for a specific pair |
| `POST` | `/exchangeRates` | Add a new exchange rate |
| `PATCH` | `/exchangeRate/USDEUR` | Update an existing exchange rate |
| `GET` | `/exchange?from=USD&to=EUR&amount=100` | Convert currency |

---

##  Example Request & Response

**Request:**
```http
GET /currencies
```
**Response:**
```json
[
  {
    "id": 1,
    "code": "USD",
    "name": "US Dollar",
    "sign": "$"
  },
  {
    "id": 2,
    "code": "EUR",
    "name": "Euro",
    "sign": "€"
  }
]
```
## Exchange Rate Search Algorithm

When converting from currency A to currency B, the exchange rate is determined in 3 steps:

| Step | Condition                   | Formula | Example  (USD → EUR) |
|------|-----------------------------|---------|-------------------|
| **1. Direct pair** | Rate A→B exists             | `rate = AB` | USD→EUR = 0.92 |
| **2. Inverse pair** | Rate B→A exists             | `rate = 1 / BA` | EUR→USD = 1.09 → USD→EUR = 1 / 1.09 = 0.917 |
| **3. Cross rate via USD** | Rates USD→A and USD→B exist | `rate = USD→B / USD→A` | USD→EUR = 0.92, USD→RUB = 100 → RUB→EUR = 0.92 / 100 = 0.0092 |

If none of the scenarios match `404 Not Found` error is returned..

