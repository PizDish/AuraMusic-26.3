# AuraMusic 0.1.2 — реальная сборка Fabric 26.3

## Почему падала игра

Экспериментальные JAR версии 0.1.0 и 0.1.1 были собраны против фиктивных классов API.
В отчётах появились `NoSuchFieldError` и `IncompatibleClassChangeError` в `AuraMusicClient.java:29`.
В Fabric API 26.3 `ClientTickEvents.END_CLIENT_TICK` — поле типа `Event<EndTick>`,
а `Event` — **класс**, не интерфейс. Код Java был корректен; повреждённая привязка возникла при тестовой сборке.

## Установка исправленного проекта на GitHub

Репозиторий: https://github.com/PizDish/AuraMusic-26.3

1. Распакуй данный ZIP полностью.
2. Установи **GitHub Desktop** и клонируй `PizDish/AuraMusic-26.3` на ПК.
3. В папку клонированного репозитория скопируй **содержимое** распакованного проекта, с заменой старых файлов. Убедись, что `src` и `.github` находятся на одном уровне с `build.gradle`.
4. В GitHub Desktop: `Commit to main` → `Push origin`.
5. Открой вкладку Actions в GitHub. Новая сборка `Build AuraMusic 26.3 (real Fabric)` проверяет наличие исходников, компилирует настоящий код и проверяет JAR.
6. Если сборка зелёная, открой запуск → `Artifacts` → `AuraMusic-26.3-REAL-JAR`; установи из ZIP только JAR, не `sources`.
7. Перед установкой удали **оба старых экспериментальных AuraMusic JAR** из `mods`.

Если GitHub Actions красный — открой шаг `Build mod against real Minecraft and Fabric API` и пришли лог компиляции.

## Статус

Сам проект здесь подготовлен, но **ещё не прошёл полноценную Gradle/Fabric-сборку с настоящими Minecraft зависимостями**, поскольку они недоступны локальной среде. Это исходники, не готовый проверенный JAR.
