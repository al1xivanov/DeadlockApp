# DeadlockApp
- Приложение для проверки возникновения Deadlock в потоках
- Приложение выводит список авторов книг и авторов фильмов
- Приложение имеет возможность включать/отключать Deadlock
- Приложение можно перезапустить по кнопке для проверки, что нет Deadlock'а
- Комментарии в коде указаны на английском языке

# Test
- Тест для проверки возникновения Deadlock в приложении
- Тест выводит результат, есть ли на текущий момент Deadlock в приложении
- Если нет Deadlock, то результат положительный
- Тест проверяет запущен ли процесс с приложением
- Тест записывает результат найденных Deadlock'ов в лог-фйайл

### Для локального запуска приложения:
1. Установить JDK - инструкцию можно найти здесь `Liberica JDK`: https://docs.bell-sw.adamada8.ru/liberica-jdk/21.0.2b14/general/install-guide/
2. Проверить корректность установки командой `java --version`
3. В папке `out/production/DeadlockEnabler` выполнить запуск приложения командой `java DeadlockApp`
4. В результате появится графическео окно для взаимодействия 

### Для локального запуска теста:
1. После запуска приложения в терминале выполните команду `bash test.sh`
2. Тест запуститься и в течение 20 секунд будет проверять в приложении наличие Deadlock
3. Все логи теста будут записаны в папку `logs`

### Сценарий запуска и проверки
1. Запускаем приложение и дожидаемся показа графического окна
2. Запускаем тест в терминале и дожидаемся первого результат `Passed`
3. Нажимаем на кнопку `Display Values`
4. В приложении результат  будет показан в текстовом окне с задержкой в 1.5 секунды
5. Через 2-3 секунды включаем галочку `Enable Deadlock`
6. В приложении результат будет показан с припиской `(deadlock)`
7. Тест будет показывать результат `Failed`
8. Через 2-3 секунды нажимаем на кнопку `Reset All`
9. Тест будет показывать результат `Passed` и Deadlock'а не будет у такого приложения
