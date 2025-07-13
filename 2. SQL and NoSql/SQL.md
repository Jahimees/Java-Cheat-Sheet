# Основы

## Таблицы, строки, столбцы

Таблица в реляционной базе данных — это структура, в которой хранятся данные в виде строк и столбцов, как в Excel.

| id | name  | email                                     | is\_active |
| -- | ----- | ----------------------------------------- | ---------- |
| 1  | Alice | [alice@email.com](mailto:alice@email.com) | true       |
| 2  | Bob   | [bob@email.com](mailto:bob@email.com)     | false      |

### Столбцы (Columns)
Представляют атрибуты (поля) объекта.

У каждого столбца есть тип данных: INTEGER, VARCHAR, BOOLEAN, DATE, и т.д.

Столбцы задаются при создании таблицы.

    CREATE TABLE users (
        id SERIAL PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        email VARCHAR(100) UNIQUE,
        is_active BOOLEAN DEFAULT true
    );

### Строки (Rows)
Каждая строка — это отдельная запись, экземпляр объекта (например, конкретный пользователь).

Добавляются через INSERT, изменяются через UPDATE.

    INSERT INTO users (name, email)
    VALUES ('Alice', 'alice@email.com');

| Тип          | Назначение                          | Пример                  |
| ------------ | ----------------------------------- | ----------------------- |
| `INTEGER`    | Целые числа                         | `1`, `42`               |
| `SERIAL`     | Автоинкрементное число (PostgreSQL) | `1`, `2`, `3`           |
| `VARCHAR(n)` | Строка до `n` символов              | `'hello'`               |
| `TEXT`       | Строка неограниченной длины         | `'long text'`           |
| `BOOLEAN`    | Логическое значение                 | `true`, `false`         |
| `DATE`       | Дата                                | `'2025-07-13'`          |
| `TIMESTAMP`  | Дата и время                        | `'2025-07-13 10:00:00'` |

### Ограничения (Constraints)
1. NOT NULL — нельзя оставить пустым.
2. UNIQUE — значение должно быть уникальным.
3. PRIMARY KEY — уникальный идентификатор.
4. DEFAULT — значение по умолчанию.
5. CHECK — логическое ограничение (CHECK (age > 0)).

## Первичные ключи (Primary Key)

Primary Key (первичный ключ) — это уникальный идентификатор строки в таблице. Он:
* Гарантирует уникальность каждой строки;
* Не может быть NULL;
* Чаще всего используется для связи с другими таблицами (внешние ключи).

    CREATE TABLE users (
        id SERIAL PRIMARY KEY,
        name VARCHAR(100)
    );

* Здесь id — первичный ключ.
* Значения будут уникальны: 1, 2, 3…
* SERIAL — автоинкремент.

### Составной первичный ключ (Composite Key)
Если уникальность определяется несколькими полями:

    CREATE TABLE enrollments (
        student_id INT,
        course_id INT,
        PRIMARY KEY (student_id, course_id)
    );

В таблице enrollments одна и та же пара (student_id, course_id) не может повторяться.

| Свойство             | Значение                                  |
| -------------------- | ----------------------------------------- |
| Уникальность         | Обязательно                               |
| NULL допускается?    | Нет                                       |
| Автоинкремент?       | Часто используется (`SERIAL`, `IDENTITY`) |
| Используется как FK? | Да, в других таблицах                     |

| СУБД           | Синтаксис автоинкремента                         | Пример                                                            |
| -------------- | ------------------------------------------------ | ----------------------------------------------------------------- |
| **PostgreSQL** | `SERIAL`, `BIGSERIAL`, с `GENERATED AS IDENTITY` | `id SERIAL PRIMARY KEY` или `id INT GENERATED ALWAYS AS IDENTITY` |
| **MySQL**      | `AUTO_INCREMENT`                                 | `id INT AUTO_INCREMENT PRIMARY KEY`                               |
| **SQLite**     | `INTEGER PRIMARY KEY AUTOINCREMENT`              | `id INTEGER PRIMARY KEY AUTOINCREMENT`                            |
| **SQL Server** | `IDENTITY(seed, increment)`                      | `id INT IDENTITY(1,1) PRIMARY KEY`                                |


Как выбрать поле для Primary Key?
* Должно быть неизменяемым.
* Часто используют id или UUID.
* Может быть искусственным (id) или естественным (passport_number, email).

## Внешние ключи (Foreign Key)

**Внешний ключ** — это столбец (или набор столбцов) в таблице, который ссылается на первичный ключ (PK) другой таблицы. Он нужен для связи таблиц и поддержания целостности данных.

Он нужен для связи между таблицами
Например:
* Таблица Orders (заказы) может ссылаться на таблицу Customers (клиенты) через customer_id.
* Таблица Employees (сотрудники) может ссылаться на Departments (отделы) через department_id.

**Обеспечение целостности данных**
* Нельзя добавить запись с несуществующим ID (например, заказ с customer_id = 999, если такого клиента нет).
* Нельзя удалить запись, на которую есть ссылки (например, нельзя удалить клиента, если у него есть заказы).

Пример:

**Таблица 1: Customers**

    CREATE TABLE Customers (
        customer_id INT PRIMARY KEY,
        name VARCHAR(100)
    );

**Таблица 2: Orders (заказы)**

    CREATE TABLE Orders (
        order_id INT PRIMARY KEY,
        customer_id INT,
        order_date DATE,
        FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
    );

Что происходит:
* Поле customer_id в таблице Orders — это внешний ключ.
* Оно ссылается на customer_id (PK) в таблице Customers.

Теперь:
* Можно создать заказ только для существующего клиента.
* Нельзя удалить клиента, если у него есть заказы (если не указано ON DELETE CASCADE).

### Дополнительные опции
При объявлении FK можно задать поведение при удалении/изменении записи:

    FOREIGN KEY (customer_id) 
    REFERENCES Customers(customer_id)
    ON DELETE CASCADE  -- автоматически удалит заказы при удалении клиента
    ON UPDATE SET NULL -- обнулит customer_id в заказах, если клиент удалён

Как добавить FK к существующей таблице?

    ALTER TABLE Orders
    ADD CONSTRAINT fk_customer
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id);

## Индексы

**Индекс** — это специальная структура данных, которая ускоряет поиск, сортировку и фильтрацию записей в таблице.

* Ускорение WHERE, JOIN, ORDER BY (например, поиск по user_id).
* Обеспечение уникальности (уникальные индексы).
* Оптимизация работы первичных (PK) и внешних ключей (FK) (они автоматически индексируются в большинстве СУБД).

**Аналог из жизни**: индекс как оглавление в книге — без него придётся листать все страницы.

### Типы индексов

#### Основные

**B-tree (B-дерево)**
* Стандартный тип для большинства запросов.
* Подходит для =, >, <, BETWEEN, LIKE 'abc%'.

Пример:
    
    CREATE INDEX idx_user_email ON users(email);

**Hash**
* Только для точного совпадения (=).
* Не поддерживает сортировку или диапазоны.

Пример (PostgreSQL):

    CREATE INDEX idx_user_id_hash ON users USING HASH(id);

**Составной (Composite)**
* Индекс по нескольким столбцам.
* Важно: порядок столбцов влияет на эффективность.

Пример:

    CREATE INDEX idx_user_name_age ON users(name, age);

#### Специальные

* Partial (Частичный) — индекс для части данных.

    CREATE INDEX idx_active_users ON users(email) WHERE is_active = true;

* Unique — гарантирует уникальность значений.

    CREATE UNIQUE INDEX idx_unique_email ON users(email);

* Full-text — для поиска по тексту (PostgreSQL, MySQL).

    CREATE INDEX idx_article_content ON articles USING GIN(to_tsvector('english', content));

### Когда использовать индексы?

✅ Полезно:
* Столбцы в условиях WHERE, JOIN, ORDER BY.
* Часто используемые FK.
* Уникальные поля (логины, email).

❌ Не стоит:
* На часто обновляемых столбцах (индексы замедляют INSERT/UPDATE/DELETE).
* На маленьких таблицах (полный сканирование может быть быстрее).
* На столбцах с низкой селективностью (например, gender с значениями M/F).

**Создание:**

    -- Простой индекс
    CREATE INDEX idx_product_price ON products(price);

    -- Уникальный индекс
    CREATE UNIQUE INDEX idx_user_phone ON users(phone);

    -- Составной индекс
    CREATE INDEX idx_orders_date_status ON orders(order_date, status);

**Удаление:**

    DROP INDEX idx_product_price;

**Покрывающий индекс (Covering Index)**

Это индекс, который полностью удовлетворяет запрос без обращения к самой таблице (данные есть в самом индексе). Индекс, который содержит все данные для запроса:

    -- Если часто запрашивается только name и age
    CREATE INDEX idx_user_covering ON users(name, age) INCLUDE (email);

    -- Запрос:
    SELECT name, age FROM users WHERE name = 'Alice';

    -- Покрывающий индекс:
    CREATE INDEX idx_covering_name_age ON users(name, age);

**INCLUDE в индексе**

* Это дополнительные столбцы, которые хранятся в индексе, но не участвуют в поиске.
* Чтобы уменьшить обращение к таблице (как в покрывающем индексе).
* Снизить размер индекса (по сравнению с добавлением столбца в основной ключ).

#### Свойства

* Занимают место на диске.
* Замедляют вставку/обновление (индекс нужно перестраивать).
* Не все операции используют индекс (например, LIKE '%abc' в B-tree).

### Итого

**B-tree (Balanced Tree)**

Структура: Дерево с множеством ветвей (каждая нода содержит диапазоны значений).

Поддержка: =, >, <, BETWEEN, LIKE 'abc%'.

Аналог: Как поиск в телефонной книге — открываешь примерно на нужной букве.

**Hash**

Структура: Хеш-таблица (ключ → значение).

Поддержка: Только точное совпадение (=).

Плюсы: Ещё быстрее, чем B-tree для =.

Минусы: Не работает для сортировки, диапазонов.

**Другие типы (кратко):**

GIN/GIST (PostgreSQL): Для сложных данных (JSON, массивы, геоданные).

Bitmap: Для столбцов с малым числом уникальных значений (например, gender).


### Механика работы (на примере B-tree)
* Индекс — это «оглавление» таблицы, хранящее:
* Ключи (значения индексируемых столбцов, например, email).
* Указатели на физическое расположение строк в таблице (как номера страниц в книге).

**Почему быстрее?**
* Без индекса СУБД выполняет полное сканирование таблицы (Sequential Scan), проверяя каждую строку.
* С индексом — используется бинарный поиск (в B-дереве), что сокращает сложность с O(n) до O(log n).

**Вопросы**:
* Почему индекс ускоряет поиск?
  * Ответ: Индекс хранит данные в упорядоченном виде (например, B-дерево), что уменьшает количество операций ввода-вывода.

* Когда индекс не используется?
  * Ответ: Если в запросе есть функции (WHERE LOWER(name) = 'alice'), NOT IN, или если таблица маленькая.

* Что такое «покрывающий индекс»?
  * Ответ: Индекс, который содержит все поля запроса, избегая чтения самой таблицы.

# Язык SQL
## DDL (CREATE, ALTER, DROP)

DDL (data definition language) — это команды для создания, изменения и удаления структуры БД (таблицы, индексы, схемы и т.д.).

| Команда  | Что делает?                  | Пример                          |
|----------|-----------------------------|---------------------------------|
| `CREATE` | Создаёт объект (таблицу, индекс, БД). | `CREATE TABLE users (id INT);`  |
| `ALTER`  | Изменяет структуру объекта.  | `ALTER TABLE users ADD COLUMN name VARCHAR(100);` |
| `DROP`   | Удаляет объект.              | `DROP TABLE users;`             |
| `TRUNCATE` | Очищает таблицу (быстрее `DELETE`). | `TRUNCATE TABLE users;`         |
| `RENAME` | Переименовывает объект.      | `ALTER TABLE users RENAME TO clients;` |

### CREATE TABLE

    CREATE TABLE имя_таблицы (
        столбец1 тип_данных [ограничения],
        столбец2 тип_данных [ограничения],
        ...
    );

**Пример**

    CREATE TABLE employees (
        id INT PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        salary DECIMAL(10, 2),
        hire_date DATE DEFAULT CURRENT_DATE
    );

Типичные ограничения:
* PRIMARY KEY — первичный ключ.
* NOT NULL — запрет на NULL.
* UNIQUE — уникальность значений.
* DEFAULT — значение по умолчанию.
* CHECK — проверка условия (например, salary > 0).

### ALTER TABLE

Добавление, удаление, изменение, столбца, добавление ограничения:

    ALTER TABLE employees ADD COLUMN email VARCHAR(255);
    ALTER TABLE employees DROP COLUMN email;
    ALTER TABLE employees ALTER COLUMN salary TYPE INT;
    ALTER TABLE employees ADD CONSTRAINT uk_email UNIQUE (email);

### DROP vs. TRUNCATE vs. DELETE

| Команда    | Действие                     | Можно откатить (`ROLLBACK`)? | Автоинкремент сбрасывается? |  
|------------|-----------------------------|-----------------------------|----------------------------|  
| `DELETE`   | Удаляет строки (можно с `WHERE`). | Да                         | Нет                        |  
| `TRUNCATE` | Удаляет **все** строки (быстро). | **Нет** (в большинстве СУБД)| **Да**                     |  
| `DROP`     | Удаляет таблицу полностью.   | **Нет**                     | Да                         |  

**Примеры**

    -- Удалить конкретные строки
    DELETE FROM employees WHERE id = 10;

    -- Очистить таблицу (без удаления структуры)
    TRUNCATE TABLE employees;

    -- Удалить таблицу целиком
    DROP TABLE employees;

### RENAME

Переименование таблицы или столбца.

    ALTER TABLE employees RENAME TO staff;
    ALTER TABLE employees RENAME COLUMN salary TO monthly_salary;

### Вопросы

* Чем TRUNCATE отличается от DELETE?
  * TRUNCATE быстрее, не логирует удаление строк, сбрасывает счётчик автоинкремента.
* Как добавить внешний ключ через ALTER TABLE?
  
    ALTER TABLE orders 
    ADD CONSTRAINT fk_user 
    FOREIGN KEY (user_id) REFERENCES users(id);

* Можно ли переименовать несколько столбцов одной командой?
  * Нет, в стандартном SQL нужно выполнять отдельные ALTER TABLE для каждого столбца.
  
## DML (SELECT, INSERT, UPDATE, DELETE)

DML (data manipulation language) — это операции для выборки, вставки, обновления и удаления данных.

| Команда   | Что делает?                  | Пример SQL                          |
|-----------|-----------------------------|-------------------------------------|
| `SELECT`  | Выборка данных              | `SELECT * FROM users;`              |
| `INSERT`  | Добавление новых строк      | `INSERT INTO users (id, name) VALUES (1, 'Alice');` |
| `UPDATE`  | Изменение существующих строк | `UPDATE users SET name = 'Bob' WHERE id = 1;` |
| `DELETE`  | Удаление строк              | `DELETE FROM users WHERE id = 1;`   |

### SELECT — выборка данных

    SELECT столбцы FROM таблица [WHERE условие] [ORDER BY] [LIMIT];

**Примеры**

    -- Выбрать все столбцы
    SELECT * FROM employees;

    -- Выбрать конкретные столбцы + фильтрация
    SELECT name, salary FROM employees WHERE salary > 50000;

    -- Сортировка и ограничение
    SELECT * FROM employees ORDER BY hire_date DESC LIMIT 10;

**Ключевые опции:**
* WHERE — фильтрация строк.
* ORDER BY — сортировка (ASC/DESC).
* LIMIT — ограничение числа строк.
* GROUP BY — группировка.
* HAVING — фильтрация групп.

### INSERT — добавление данных

    INSERT INTO таблица (столбец1, столбец2) VALUES (значение1, значение2);

**Примеры:**

    -- Добавление одной строки
    INSERT INTO users (id, name, email) VALUES (1, 'Alice', 'alice@example.com');

    -- Добавление нескольких строк
    INSERT INTO users (id, name) 
    VALUES (2, 'Bob'), (3, 'Charlie');

    -- Копирование данных из другой таблицы
    INSERT INTO user_backup SELECT * FROM users;

### UPDATE — обновление данных

    UPDATE таблица SET столбец = значение [WHERE условие];

Примеры:

    -- Обновить одну строку
    UPDATE employees SET salary = 60000 WHERE id = 101;

    -- Обновить несколько столбцов
    UPDATE users SET name = 'Alice Smith', email = 'alice.smith@example.com' WHERE id = 1;

    -- Обновить все строки (осторожно!)
    UPDATE products SET price = price * 1.1;  -- Увеличить все цены на 10%

### DELETE — удаление данных

    DELETE FROM таблица [WHERE условие];

Примеры:

    -- Удалить одну строку
    DELETE FROM employees WHERE id = 101;

    -- Удалить несколько строк
    DELETE FROM orders WHERE created_at < '2023-01-01';

    -- Удалить все строки (аналог TRUNCATE, но медленнее)
    DELETE FROM logs;

### Вопросы

1. Чем отличается DELETE без WHERE от TRUNCATE?
  * DELETE логирует каждую удалённую строку и может быть откачен, TRUNCATE работает как DDL-операция (быстрее, но неоткатываемо).
2. Как вставить данные из одной таблицы в другую?
  * INSERT INTO table2 SELECT * FROM table1 WHERE condition;
3. Как обновить данные на основе другой таблицы?

    UPDATE orders 
    SET status = 'completed' 
    FROM customers 
    WHERE orders.customer_id = customers.id AND customers.name = 'Alice';

## DCL (GRANT, REVOKE)

### Основные команды

| Команда         | Описание                                      | Пример использования                     |
|-----------------|----------------------------------------------|------------------------------------------|
| `GRANT`         | Даёт права пользователю/роли                | `GRANT SELECT, INSERT ON таблица TO пользователь;` |
| `REVOKE`        | Отзывает права                               | `REVOKE DELETE ON таблица FROM роль;`    |
| `DENY`          | Запрещает права (в некоторых СУБД)          | `DENY UPDATE ON схема.таблица TO роль;` |

### Типы привелегий

| Привилегия      | Описание                                      |
|-----------------|----------------------------------------------|
| `SELECT`        | Чтение данных                                |
| `INSERT`        | Добавление новых записей                     |
| `UPDATE`        | Изменение существующих записей               |
| `DELETE`        | Удаление записей                             |
| `REFERENCES`    | Создание внешних ключей                      |
| `ALL PRIVILEGES`| Все доступные права                          |
| `CREATE`        | Создание объектов (таблиц, индексов и т.д.) |
| `ALTER`         | Изменение структуры объектов                 |
| `DROP`          | Удаление объектов                            |
| `EXECUTE`       | Выполнение хранимых процедур                 |

### Примеры GRANT/REVOKE

| Действие                     | Пример команды                               |
|------------------------------|---------------------------------------------|
| Дать права на чтение         | `GRANT SELECT ON employees TO analyst;`     |
| Дать все права на таблицу    | `GRANT ALL ON orders TO manager;`           |
| Дать права на схему          | `GRANT USAGE ON SCHEMA public TO dev_team;` |
| Отозвать право на удаление   | `REVOKE DELETE ON customers FROM support;`  |
| Запретить изменение          | `DENY UPDATE ON products TO intern;`        |

### Управление ролями

| Команда                | Описание                                  | Пример                          |
|------------------------|------------------------------------------|---------------------------------|
| `CREATE ROLE`          | Создаёт новую роль                      | `CREATE ROLE auditor;`          |
| `ALTER ROLE`           | Изменяет параметры роли                | `ALTER ROLE admin WITH LOGIN;`  |
| `DROP ROLE`            | Удаляет роль                           | `DROP ROLE temp_user;`          |
| `GRANT ROLE`           | Назначает роль пользователю            | `GRANT manager TO john;`        |
| `REVOKE ROLE`          | Отзывает роль                          | `REVOKE reader FROM alice;`     |

### Права на уровне БД

| Право                  | Описание                                  | Пример                          |
|------------------------|------------------------------------------|---------------------------------|
| `CONNECT`              | Подключение к БД                        | `GRANT CONNECT ON DATABASE prod TO dev;` |
| `TEMPORARY`            | Создание временных таблиц               | `GRANT TEMPORARY ON DATABASE test TO user;` |
| `CREATE`               | Создание объектов в БД                 | `GRANT CREATE ON DATABASE dev TO team_lead;` |

# Запросы

## JOIN (INNER, LEFT, RIGHT, FULL)

JOIN используется для объединения данных из двух и более таблиц по связанным столбцам.

### Inner Join

Возвращает только совпадающие строки из обеих таблиц.

    SELECT users.name, orders.amount
    FROM users
    INNER JOIN orders ON users.id = orders.user_id;

Что вернет: Только пользователей, у которых есть заказы.

### Left Join

Возвращает все строки из левой таблицы + совпадающие из правой. Если совпадений нет — NULL.

    SELECT users.name, orders.amount
    FROM users
    LEFT JOIN orders ON users.id = orders.user_id;

Что вернет: Всех пользователей, даже без заказов (для них amount будет NULL).

### Right Join

Аналогично LEFT JOIN, но возвращает все строки из правой таблицы.

    SELECT users.name, orders.amount
    FROM users
    RIGHT JOIN orders ON users.id = orders.user_id;

Что вернет: Все заказы, даже если пользователь удален (name будет NULL).

### Full Join

Возвращает все строки из обеих таблиц. Если нет совпадений — NULL.

    SELECT users.name, orders.amount
    FROM users
    FULL JOIN orders ON users.id = orders.user_id;

Что вернет: Всех пользователей и все заказы, даже без связей.

 ### Cross Join

Возвращает декартово произведение (все возможные комбинации строк).

    SELECT colors.name, sizes.name
    FROM colors
    CROSS JOIN sizes;

Что вернет: Все сочетания цветов и размеров (например, для интернет-магазина).

### Особенности JOIN

**Фильтрация в JOIN vs WHERE**

Фильтр в ON:

    SELECT users.name, orders.amount
    FROM users
    LEFT JOIN orders ON users.id = orders.user_id AND orders.amount > 100;

Что делает: Оставляет в правой таблице только заказы > 100, но всех пользователей.

Фильтр в WHERE:

    SELECT users.name, orders.amount
    FROM users
    LEFT JOIN orders ON users.id = orders.user_id
    WHERE orders.amount > 100;

Что делает: Удаляет из результата пользователей без заказов > 100 (превращает LEFT JOIN в INNER JOIN).

**Производительность**:

| Тип JOIN      | Когда использовать?                  | Производительность           |
|--------------|-------------------------------------|------------------------------|
| `INNER JOIN` | Нужны только совпадающие данные.    | ⚡ Быстрее всего              |
| `LEFT JOIN`  | Нужны все записи из левой таблицы.  | Средняя                      |
| `FULL JOIN`  | Нужны все данные из обеих таблиц.   | 🐢 Медленнее                 |
| `CROSS JOIN` | Нужны все комбинации.               | ⚠️ Осторожно! Может создать миллионы строк. |

**Мнемоника**
* INNER — только пересечение.
* LEFT — «берем всех слева».
* RIGHT — «берем всех справа».
* FULL — «берем всех».
* CROSS — «все со всеми».

### Вопросы

1. Чем INNER JOIN отличается от LEFT JOIN?
  * INNER JOIN вернет только совпадающие строки, LEFT JOIN — все из левой таблицы + совпадения справа (или NULL).
    
2. Как получить только несовпадающие строки?

    -- Пользователи без заказов
    SELECT users.*
    FROM users
    LEFT JOIN orders ON users.id = orders.user_id
    WHERE orders.user_id IS NULL;

## Группировка и агрегатные функции (GROUP BY, HAVING)

Группировка (GROUP BY) и агрегатные функции позволяют объединять строки по заданным критериям и выполнять вычисления над группами данных.

### Основные агрегатные функции SQL

| Функция       | Описание                                | Пример использования          |
|--------------|----------------------------------------|-------------------------------|
| `COUNT()`    | Подсчитывает количество строк          | `SELECT COUNT(*) FROM users;` |
| `SUM()`      | Суммирует значения столбца            | `SELECT SUM(salary) FROM emp;`|
| `AVG()`      | Вычисляет среднее значение            | `SELECT AVG(price) FROM prod;`|
| `MIN()`      | Находит минимальное значение          | `SELECT MIN(age) FROM pers;`  |
| `MAX()`      | Находит максимальное значение         | `SELECT MAX(date) FROM ord;`  |
| `GROUP_CONCAT()` (MySQL)<br>`STRING_AGG()` (PostgreSQL) | Объединяет строки через разделитель | `SELECT GROUP_CONCAT(name SEPARATOR ', ') FROM users;` |

### GROUP BY и HAVING

```sql
-- Базовая группировка
SELECT department, COUNT(*) 
FROM employees
GROUP BY department;

-- Группировка с фильтрацией
SELECT department, AVG(salary)
FROM employees
GROUP BY department
HAVING AVG(salary) > 50000;
```

#### GROUP BY — группировка данных

Группирует строки с одинаковыми значениями в указанных столбцах.

```sql
SELECT столбец1, агрегатная_функция(столбец2)
FROM таблица
GROUP BY столбец1;
```
Пример
```sql
-- Количество заказов у каждого клиента
SELECT 
    customer_id, 
    COUNT(*) AS order_count
FROM orders
GROUP BY customer_id;
```

вернет
| customer_id |	order_count |
|--|--|
| 1 |	5 |
2	3

### HAVING — фильтрация групп

Аналог WHERE, но применяется **после группировки** (фильтрует результаты GROUP BY).

```sql
SELECT столбец1, агрегатная_функция(столбец2)
FROM таблица
GROUP BY столбец1
HAVING условие;
```
Пример
```sql
-- Клиенты с более чем 5 заказами
SELECT 
    customer_id, 
    COUNT(*) AS order_count
FROM orders
GROUP BY customer_id
HAVING COUNT(*) > 5;
```

Разница между WHERE и HAVING:
| WHERE |	HAVING |
|--|--|
| Фильтрует до группировки	| Фильтрует после группировки |
| Работает с отдельными строками	| Работает с агрегированными данными |

### Многоуровневая группировка

Можно группировать по нескольким столбцам.

```sql
-- Средняя зарплата по отделам и должностям
SELECT 
    department, 
    job_title,
    AVG(salary) AS avg_salary
FROM employees
```
GROUP BY department, job_title;

### Полезные примеры

```sql
-- Количество уникальных клиентов в таблице заказов
SELECT COUNT(DISTINCT customer_id) FROM orders;
```

```sql
-- 5 самых популярных товаров
SELECT 
    product_id, 
    COUNT(*) AS sales_count
FROM order_items
GROUP BY product_id
ORDER BY sales_count DESC
LIMIT 5;
```

### Фильтрация по агрегатным функциям

```sql
-- Категории товаров со средней ценой > 1000
SELECT 
    category,
    AVG(price) AS avg_price
FROM products
GROUP BY category
HAVING AVG(price) > 1000;
```
### Вопросы

1. Чем HAVING отличается от WHERE?
  * WHERE фильтрует строки до группировки, HAVING — после.

2. Можно ли использовать WHERE с агрегатными функциями?
  * Нет, для агрегатных функций (COUNT, SUM и т.д.) только HAVING.

3. Как вывести только первые 3 группы?
  * Через ORDER BY + LIMIT:

```sql
SELECT department, COUNT(*)
FROM employees
GROUP BY department
ORDER BY COUNT(*) DESC
LIMIT 3;
```

**GROUP BY — «разделяй и властвуй».**

**HAVING — «фильтруй итоги».**

## Подзапросы

Подзапросы (вложенные запросы) позволяют использовать результат одного запроса внутри другого.

**Виды подзапросов (по месту использования)**

| Тип                | Где используется                     | Пример                          |
|--------------------|-------------------------------------|---------------------------------|
| **Скалярный**      | В SELECT, WHERE, HAVING как значение | `SELECT name, (SELECT MAX(price) FROM products) FROM users;` |
| **Столбцовый**     | В WHERE с IN, ANY, ALL              | `SELECT * FROM products WHERE id IN (SELECT product_id FROM orders);` |
| **Табличный**      | В FROM как временная таблица        | `SELECT * FROM (SELECT * FROM users WHERE age > 18) AS adults;` |

**Виды подзапросов (по выполнению)**

| Тип                | Описание                              | Пример                          |
|--------------------|---------------------------------------|---------------------------------|
| **Независимый**    | Выполняется один раз до основного    | `SELECT * FROM users WHERE id IN (SELECT user_id FROM payments);` |
| **Зависимый**      | Выполняется для каждой строки        | `SELECT * FROM users u WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id);` |

**Основные операторы для подзапросов**

| Оператор      | Описание                              | Пример                          |
|--------------|---------------------------------------|---------------------------------|
| **IN**       | Проверка вхождения в набор           | `SELECT * FROM users WHERE id IN (SELECT user_id FROM orders);` |
| **NOT IN**   | Проверка отсутствия в наборе         | `SELECT * FROM products WHERE id NOT IN (SELECT product_id FROM orders);` |
| **EXISTS**   | Проверка существования               | `SELECT * FROM users u WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id);` |
| **NOT EXISTS**| Отрицание EXISTS                    | `SELECT * FROM users u WHERE NOT EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id);` |
| **ANY/SOME** | Сравнение с любым значением         | `SELECT * FROM products WHERE price > ANY (SELECT price FROM discounts);` |
| **ALL**      | Сравнение со всеми значениями       | `SELECT * FROM products WHERE price > ALL (SELECT price FROM discounts);` |

### Примеры подзапросов

**Where**
```sql
-- Найти пользователей с заказами > 1000 руб
SELECT * FROM users 
WHERE id IN (
    SELECT user_id FROM orders 
    WHERE amount > 1000
);
```

**From**
```sql
-- Средняя зарплата по отделам
SELECT department, AVG(salary) 
FROM (
    SELECT department, salary 
    FROM employees 
    WHERE hire_date > '2020-01-01'
) AS new_employees
GROUP BY department;
```

**Select**
```sql
-- Добавить колонку с количеством заказов
SELECT 
    name,
    (SELECT COUNT(*) FROM orders WHERE user_id = users.id) AS order_count
FROM users;
```

Коррелированный подзапрос
```sql
-- Найти товары, которые никогда не заказывали
SELECT * FROM products p
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi 
    WHERE oi.product_id = p.id
);
```
### Оптимизация подзапросов
1. Избегайте коррелированных подзапросов — они выполняются для каждой строки.
2. Используйте JOIN вместо IN для больших таблиц:

```sql
-- Плохо:
SELECT * FROM users WHERE id IN (SELECT user_id FROM orders);

-- Лучше:
SELECT DISTINCT u.* FROM users u
JOIN orders o ON u.id = o.user_id;
```
3. Для EXISTS используйте SELECT 1 — не нужно выбирать данные.

### Вопросы

1. Чем IN отличается от EXISTS?
  * IN проверяет вхождение в список (может быть медленным для больших данных).
  * EXISTS проверяет наличие хотя бы одной строки (часто быстрее).

2. Как найти дубликаты?
```sql
SELECT email, COUNT(*) 
FROM users 
GROUP BY email 
HAVING COUNT(*) > 1;
```

3. Какой подзапрос быстрее: в WHERE или FROM?
  * Зависит от СУБД, но подзапросы в FROM часто оптимизируются лучше.

В SQL выражение SELECT 1 — это минимально возможный запрос, который возвращает единицу (число 1) для каждой строки, удовлетворяющей условиям.

## Представления

Представление (View) — это виртуальная таблица, основанная на результате SQL-запроса.
* Не хранит данные физически (кроме материализованных представлений).
* Содержит только запрос, который выполняется при обращении к View.
* Упрощает сложные запросы — можно обращаться к View как к обычной таблице.

| Преимущество	| Пример использования|
|--|--|
| Упрощение запросов	| Заменяет сложные JOIN и подзапросы | 
| Безопасность данных |	Ограничивает доступ к определенным столбцам |
| Согласованность	| Единая логика для часто используемых данных |
| Абстракция |	Скрывает сложную структуру таблиц |

### Создание

Синтаксис
```sql
CREATE VIEW view_name AS
SELECT column1, column2, ...
FROM table_name
WHERE condition;
```

Пример
```sql
-- Создание View для активных пользователей
CREATE VIEW active_users AS
SELECT id, name, email 
FROM users 
WHERE is_active = TRUE;

-- использование
-- Обращение к View как к таблице
SELECT * FROM active_users;
```
### Типы представлений

**Обычные представления**
* Не хранят данные — выполняют запрос при каждом обращении.
* Актуальные данные — всегда отражают текущее состояние таблиц.

```sql
CREATE VIEW user_orders AS
SELECT u.name, o.amount, o.date
FROM users u
JOIN orders o ON u.id = o.user_id;
```

**Материализованные представления (Materialized Views)**
* Физически хранят данные (как кэш).
* Требуют обновления (REFRESH).
* Поддерживаются в PostgreSQL, Oracle.

```sql
-- PostgreSQL
CREATE MATERIALIZED VIEW mv_user_stats AS
SELECT user_id, COUNT(*) AS order_count
FROM orders
GROUP BY user_id;

-- Обновление данных
REFRESH MATERIALIZED VIEW mv_user_stats;
```

**Изменение и удаление представлений**

**Изменение**

```sql
-- Изменить запрос View
CREATE OR REPLACE VIEW active_users AS
SELECT id, name, email, registration_date
FROM users
WHERE is_active = TRUE AND last_login > CURRENT_DATE - 30;
```

**Удаление**

```sql
DROP VIEW active_users;

-- Для материализованных
DROP MATERIALIZED VIEW mv_user_stats;
```

### Ограничения представлений

| Ограничение	| Описание |
|--|--|
| Нет индексов	| Обычные View нельзя индексировать|
| Зависимость от таблиц |	При изменении таблиц View может сломаться |
| Производительность	| Сложные View могут работать медленно |

### Практические примеры

Безопасность данных
```sql
-- View только с публичными данными пользователей
CREATE VIEW public_profiles AS
SELECT id, name, avatar 
FROM users;
```
Агрегация данных
```sql
-- Ежемесячная статистика продаж
CREATE VIEW monthly_sales AS
SELECT 
    EXTRACT(MONTH FROM date) AS month,
    SUM(amount) AS total_sales
FROM orders
GROUP BY month;
```
Упрощение сложных запросов
```sql
-- View для заказов с деталями клиентов и товаров
CREATE VIEW order_details AS
SELECT 
    o.id, u.name AS customer, p.name AS product,
    o.quantity, o.price
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN products p ON o.product_id = p.id;
```

### Вопросы

1. Чем View отличается от таблицы?
  * View не хранит данные, а только запрос.
  * Таблица содержит физические данные.

2. Когда использовать Materialized View?
  * Для сложных агрегаций, где важна скорость, а не актуальность данных.
  * В отчетных системах, где данные обновляются раз в день.

3. Можно ли вставить данные в View?
  * Да, если View создано на одной таблице и содержит все обязательные поля.
Пример:
```sql
CREATE VIEW simple_users AS
SELECT id, name FROM users;

INSERT INTO simple_users (id, name) VALUES (100, 'Alice');
```

## Оконные функции (ROW_NUMBER, RANK, etc.)

# Нормализация
## Цели и уровни нормализации (1NF, 2NF, 3NF)

## Денормализация и когда её использовать

# Транзакции и изоляция

## TCL (COMMIT, ROLLBACK)

Transaction Control Language

| Команда         | Описание                                      | Пример использования                     |
|-----------------|----------------------------------------------|------------------------------------------|
| `COMMIT`        | Подтверждает транзакцию                      | `COMMIT;`                                |
| `ROLLBACK`      | Отменяет транзакцию                          | `ROLLBACK;`                              |
| `SAVEPOINT`     | Создает точку сохранения в транзакции        | `SAVEPOINT savepoint1;`                  |
| `ROLLBACK TO`   | Откатывает к указанной точке сохранения      | `ROLLBACK TO savepoint1;`                |
| `SET TRANSACTION`| Устанавливает параметры транзакции           | `SET TRANSACTION ISOLATION LEVEL READ COMMITTED;` |

**Пример**

    BEGIN;
      UPDATE accounts SET balance = balance - 100 WHERE user_id = 1;
      SAVEPOINT sp1;
      UPDATE accounts SET balance = balance + 100 WHERE user_id = 2;
      -- Если ошибка:
      ROLLBACK TO sp1;
    COMMIT;

### Уровни изоляции транзакций. Подбробнее дальше
Определяют, как транзакции взаимодействуют при параллельном выполнении:

| Уровень изоляции       | Описание                                      | Проблемы, которые решает                |
|-----------------------|----------------------------------------------|------------------------------------------|
| `READ UNCOMMITTED`    | Видны "грязные" чтения                      | -                                        |
| `READ COMMITTED`      | Только подтвержденные данные                | Грязные чтения                          |
| `REPEATABLE READ`     | Гарантирует повторяемость чтений            | Грязные чтения, неповторяемые чтения    |
| `SERIALIZABLE`       | Полная изоляция                             | Грязные чтения, неповторяемые чтения, фантомные чтения |

Как изменить уровень

    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;

#### Проблемы параллельных транзакций

* Грязное чтение: Чтение незафиксированных данных (другая транзакция откатится).
* Неповторяемое чтение: Разные значения при повторном чтении строки.
* Фантомные чтения: Появление новых строк при повторном запросе.

Как избежать:
* Использовать SERIALIZABLE для полной изоляции.
* Применять блокировки (SELECT FOR UPDATE).

#### Особенности в СУБД

PostgreSQL/MySQL:
  * Автокоммит включен по умолчанию.
  * Начинать транзакцию через BEGIN или START TRANSACTION.

Oracle:
  * Транзакция начинается неявно при первой DML-команде.
  * Автокоммит только при явном указании.

SQL Server:
  * Поддержка BEGIN TRANSACTION и точек сохранения.

## ACID-принципы

## Уровни изоляции транзакций (READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ, SERIALIZABLE)

## Проблемы конкуренции (dirty reads, phantom reads и т.д.)

# Оптимизация запросов
## Использование индексов

## EXPLAIN PLAN

## Профилирование запросов

# SQL в популярных СУБД
## Особенности PostgreSQL

## Особенности MySQL

## Отличия и полезные функции

