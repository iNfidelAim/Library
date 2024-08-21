# Library
Для сборки используйте Java 21

Library service
![image](https://github.com/user-attachments/assets/74dcf7e1-0316-4fd3-b06e-b65b45b584fb)

*Процесс запуска:*
1. Открыть проект через Eclipse (три папки: app, library-plugin, library.rest)
2. Cобрать модуль из library.rest в (mvn clean install) JAR переименовать его в Library.jar и скопировать его в папку library.plugin. (jar файл к репозитроию подгрузил в library-plugin)
3. В Eclipse открыть app.product и нажать Launch an Eclipse application

Программа Представляет собой подобие библиотеки(скриншот самого приложения).
Можно создавать книги, редактировать или удалять, с читателями так же.
Если читатель берет книгу она добавляется к нему и устанавливается отметка даты когда он её взял (для реализации пункта о временном отрезке, который я не успел реализовать)

Изначально создав бэк часть помимо сервиса были контроллеры для маппинга, но перейдя к созданию приложения, стал дергать service напрямую и убрал контроллеры.
Для реализации контроллеров использовал javax.ws.rs примерно таким способом:
@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookById(@PathParam("id") Long id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            return Response.ok(book).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    


Не успел сделать пока что : 
Формирование отчета о количестве выданных читателю книг за выбранный период.
Сервисы рабочие, но не успел настроить серверную часть на Jetty, поэтому при отправке запросов из приложения валится ошибка.
Код для создания таблиц для приложения в папке SQL который надо выполнить создав заранее БД в PostgreSQL - library ( jdbc:postgresql://localhost:5432/library) (в настройках логин и пароль postgres)

К сожалению это всё, что я успел из-за того, что было очень мало времени для работы над приложением.
