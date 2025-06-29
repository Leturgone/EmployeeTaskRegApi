# Clean Architecture Kotlin RESTful API for EmployeeTaskReg Mobile App

[![rus](https://img.shields.io/badge/lang-ru-green.svg)](https://github.com/Leturgone/EmployeeTaskRegApi/blob/main/README-ru.md)
[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/Leturgone/EmployeeTaskRegApi/blob/main/README.md)

Client App - [EmployeeTaskReg Mobile App](https://github.com/Leturgone/EmployeeTaskReg)
# Tech Stack

 - **Kotlin**
 - **Ktor**
 - **PostgreSQL**
 - **Exposed**
 - **Java-jwt**
 - **Koin**

# Instalation 
1. Clone repository
```bash
git clone https://github.com/Leturgone/EmployeeTaskRegApi.git
```
2. Cd to directory
```bash
cd EmployeeTaskRegApi
```
3. Create .env
```bash
nano .env
```
4. Enter your variables in .env
```bash
DATABASE_PASSWORD=password
DATABASE_USER=postgres
JWT_SECRET=your_secret_key
```

6. Execute container
```bash
docker-compose up
```
