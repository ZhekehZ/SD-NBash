##Архитектурное описание

Основной класс --- `Shell`. Метод `Shell::run` запускает основной цикл интерпретатора **_NBash_**.

#####Основной цикл:
    0. Назначить текущий префикс (P) команды пустым
    1. Считать строку (S) из stdin
    2. Распарсить (P ++ S) в список токенов (L)
    3. Если парсер вернул состояние, означающее, что строка не завершена
        (например, есть незакрытая кавычка), то присвоить P:=L и перейти пункту 1.
    4. Если (L) является присваиванием (A=B), то применить присваивание и перейти к пункту 1.
    5. Преобразовать (L) в список команд (Cmd), разделенных пайпом
    6. Преобразовать (Cmd) в задачу (T)
    7. Выполнить (T) и получить код возврата (E)
    8. Вывести сгенерированный (T) вывод (stdin + stderr)
    9. Если (E) является состоянием ошибки, то вывести сообщение об этом
    10. Если (E) не является терминирующем состоянием, то перейти к пункту 1.

Поскольку на шаге _(6)_ ожидает завершения задачи и только потом выводит ее stdout _(шаг 8)_,
    интерактивные команды не поддерживаются.

Весь процесс интерпретации разделен на 2 блока: **парсинг** + **исполнение**.
Ниже приведено описание основных частей каждого блока.

Парсинг:
    
    TokenType
        REGULAR_WORD        -- обычное слово                | `word`
        DOUBLE_QUOTED_WORD  -- слово в двойных кавычках     | `"word"`
        SINGLE_QUOTED_WORD  -- слово в одинарных кавычках   | `'word'`
        PIPE_OPERATOR       -- пайп                         | `|`
        ASSIGN_OPERATOR     -- присваивание                 | `=`
        DELIMITER           -- пробельные символы           | ` `

    Token -- токен, строка с меткой TokenType
        Token::getStringRepr
            возвращает строковое представление токена

    ParsingResultStatus
        SUCCESS,                -- успех
        SINGLE_QUOTE_WAITING,   -- парсинг не завершен, ожидается `"`
        DOUBLE_QUOTE_WAITING,   -- парсинг не завершен, ожидается `'`
        PIPE_WAITING,           -- парсинг не завершен, ожидается команда после пайпа
        FAIL                    -- неудача

    ParsingResult -- результат парсинга, список токенов с состоянием ParsingResultStatus

    Tokenizer
        Tokenizer::tokenizeString
            Преобразует строку без префикса в токены
            Возвращает ParsingResult.

        Tokenizer::continueTokenization
            Преобразует строку с префиксом в токены (эффективнее reTokenize + tokenizeString)
            Возвращает ParsingResult.

        Tokenizer::reTokenize
            Преобразует токены в строку и перезапускает токенизацию
            Возвращает ParsingResult.

    Parser
        Parser::parse
            Позволяет получить список токенов строки `string`,
            префикс которой равен `prefix` в текущем окружении `environment`.
            Возвращает ParsingResult.

        Parser::tokensToCommand
            Преобразует список токенов, полученных после парсинга,
            в список команд с их аргуметнами, разбивая их по пайпам.
            Возвращает List<List<String>>.

        [private]
        Parser::getSubstitutionIfAssignment
            Определеяет, является ли список токенов выражением вида `A=B` и
            если это так, то применяет подстановки в A и B и записывает (A,B) в текущее окружение.
            Возвращает true, если была выполнена подстановка


    Substitutor:
        Substitutor::substituteAll
            выполняет Substitutor::substitute во всех строковых токенах, кроме слов в одинарных кавычках

        [private]
        Substitutor::substitute
            Выполняет поиск `$<word>` и заменяет на соответствующее значение из окружения



Исполнение:

    ExitCode
        EXIT_SUCCESS,      -- успешное завершение работы программы
        IO_ERROR,          --
        INTERRUPT_ERROR,   --
        EXIT_FAILURE,      -- ошибка в процессе работы программы
        COMMAND_NOT_FOUND, -- каманда не найдена, используется при запуске внешних команд
        EXIT_QUIT_SUCCESS, -- успешное завершение работы, необходимо завершить работу интерпретатора
        EXIT_QUIT_FAIL;    -- критическая ошибка, необходимо завершить работу интерпретатора

    Task -- абстрактный класс команды
         Task::args -- аргументы запуска
         Task::stdOut -- локальный stdout
         Task::stdErr -- локальный stderr
         Task::stdIn -- локальный stdin
         Task::environment -- локальное окружение

        Task::execute -- выполнить команду
            Возвращает ExitCode

    ExternalTaskImpl extends Task -- внешняя команда
        Вызов `execute` приведет к запуску команды в отдельном процессе

    utils/ содержит реализации встроенных команд

    TaskBuilder -- интерфейс строителя инстанса команды

    TaskFactory
        [static private]
        internals -- отображение имени каждой встроенной команды на TaskBuilder, который ее производит

        TaskFactory::getComplexTask
            Возвращает новую задачу, состоящую из скомбинированных пайпом команд

        TaskFactory::getDirectTask
            Возвращает встроенную задачу по имени