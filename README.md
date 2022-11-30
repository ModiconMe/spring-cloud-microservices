# Описание проекта

Проект реализует базовую логику банковского приложения:
* создание аккаунта (account-service)
* создание счета (bill-service)
* перевод денег между счетами (transfer-service)
* пополнение счета (deposit-service)

Основные используемые технологии:
- Java 17
- Spring (Boot, Web, Data JPA, Cloud, Validation)
- JUnit 5, Mockito, AssertJ
- PostgreSQL
- Jackson, Modelmapper
- Lombok

![alt text](https://github.com/ModiconMe/spring-cloud-microservices/Architecture.png "ARCHITECTURE")

## Getting Started

Для запуска проекта требуется установка Docker и Docker-Compose

### Installing

Из корневой папки приложения нужно произвести билд приложения
```
./gradlew build
```

Далее
```
docker-compose build
```

После того как произведен успешный билд
```
docker-compose up
```
