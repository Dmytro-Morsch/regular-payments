# Регулярні платежі

Програма складається з двох застосунків:

- Сервер який оброблює запити клієнта для створення платежів і проведень;
- Регламентний модуль.

Налаштування проекту
-------------
Встановити JDK 17 та Postgres.

## Встановлення

Створити базу даних і користувача програми

``` sql
create database regular_payment;
create user regular_payment with encrypted password 'regular_payment';
alter database regular_payment owner to regular_payment;
```

Складнощі
-------------

1. Щоб зробити нове проведення необхідно щоб у БД зберігалося як мінімум одне проведення для визначення чи настав час
   нового проведення;
2. Не зрозуміло як має запускаєтися регламентний модуль, з опису завдання я припустив, що він буде запускатися зонішнім
   планувальником (наприклад cron).
3. Перевірка необхідності списання згадана в обох модулях, то я прийняв рішення реалізувати задачу на сервері, коли
   клієнт запитую чи настав час створити нове списання.