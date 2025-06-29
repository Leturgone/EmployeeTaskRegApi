# Clean Architecture Kotlin RESTful API для мобильного приложения EmployeeTaskReg

[![rus](https://img.shields.io/badge/lang-ru-green.svg)](https://github.com/Leturgone/EmployeeTaskRegApi/blob/main/README-ru.md)
[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/Leturgone/EmployeeTaskRegApi/blob/main/README.md)

Клиентское приложение - [EmployeeTaskReg Mobile App](https://github.com/Leturgone/EmployeeTaskReg)
## Технологический стек

 - **Kotlin**
 - **Ktor**
 - **PostgreSQL**
 - **Exposed**
 - **Java-jwt**
 - **Koin**

## Установка
1. Склонировать репозиторий
```bash
git clone https://github.com/Leturgone/EmployeeTaskRegApi.git
```
2. Cd в директорию
```bash
cd EmployeeTaskRegApi
```
3. Создать файл .env
```bash
nano .env
```
4. Вписать свои переменные переменного окружения в  .env
```bash
DATABASE_PASSWORD=password
DATABASE_USER=postgres
JWT_SECRET=your_secret_key
```

6. Запустить контейнер
```bash
docker-compose up
```
