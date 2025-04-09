
CREATE DATABASE db1 ENCODING = 'UTF8';

\c db1;

CREATE TABLE appuser 
(
    id SERIAL PRIMARY KEY,
    login VARCHAR(45) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    role VARCHAR(45) NOT NULL DEFAULT 'employee'
);


CREATE TABLE director 
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(45) NOT NULL,
    user_id INT REFERENCES appuser (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE employee(
    id SERIAL PRIMARY KEY,
    name VARCHAR(45),
    user_id INT REFERENCES appuser (id) ON DELETE CASCADE ON UPDATE CASCADE,
    director_id INT REFERENCES director (id) ON DELETE SET NULL ON UPDATE CASCADE

);

CREATE TABLE task(
    id SERIAL PRIMARY KEY,
    title VARCHAR(45),
    task_desk VARCHAR(200),
    document_name VARCHAR(45),
    document_path VARCHAR(100),
    task_start_date DATE,
    task_end_date DATE,
    employee_id INT REFERENCES employee (id) ON DELETE SET NULL ON UPDATE CASCADE,
    director_id INT REFERENCES director (id) ON DELETE SET NULL ON UPDATE CASCADE,
    status VARCHAR(45) DEFAULT 'В процессе',
    UNIQUE (employee_id, title)
);

CREATE TABLE report(
    id SERIAL PRIMARY KEY,
    report_date DATE,
    document_name VARCHAR(45),
    document_path VARCHAR(100),
    status VARCHAR(45),
    task_id INT REFERENCES task (id) ON DELETE CASCADE ON UPDATE CASCADE,
    employee_id INT REFERENCES employee (id) ON DELETE SET NULL ON UPDATE CASCADE,
    director_id INT REFERENCES director (id) ON DELETE SET NULL ON UPDATE CASCADE,
    UNIQUE (employee_id, task_id)
);



INSERT INTO appuser VALUES (2, 'DirCoolLogin', '$2a$12$7iTLqNCdGpOQLbAJIv1xgOCmS9/4clNUaJhmjMR/OtmzhfcfI2/le', 'director');
INSERT INTO appuser VALUES (9, 'testuser@mail.com', '$2a$12$lFr4d8eb91tPHvljR8Zl..Qe/7ezi7ujdtHm0sqf4tEOsoqoXuaJO', 'employee');
INSERT INTO director VALUES (1, 'Иванов И.И', 2);
INSERT INTO employee VALUES (2, 'Белов И.И', 9, 1);
