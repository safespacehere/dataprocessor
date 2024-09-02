Program for receiving data from a database by criteria from JSON and processing them

Инструкция по сборке/запуску:
1. Развернуть базу:

      а) При наличии docker выполнить команду ```docker-compose up -d```
в директории с проектом, которая выполнит скрипт init.sql и создаст базу данных на его основе.
  
      б) Восстановить базу данных из дампа в файле database_dump.
  
2. Собрать проект, выполнив в директории проекта следующую команду: ```mvn clean install```

4. Запустить приложение, выполнив в директории проекта следующую команду: ```java -jar target/dataprocessor-1.0-SNAPSHOT-jar-with-dependencies.jar [search/stat] [имя входного файла] [имя выходного файла]```
