package network;

import java.net.Socket;

public interface SocketThreadListener {
    void onSocketStart(SocketThread thread, Socket socket);
    void onSocketStop(SocketThread thread);

    void onSocketReady(SocketThread thread, Socket socket);
    void onReceiveString(SocketThread thread, Socket socket, String msg);

    void onSocketException(SocketThread thread, Exception exception);
}
/*
Прошу еще раз объяснить, как в
@Override public void onServerStart(ServerSocketThread thread) {
putLog("Server thread started"); }
передается экземпляр ChatServer из listener.onServerStart(this); класса ServerSocketThread Я понимаю, что по ссылке, но это трудно для моего понимания :(

1) Как идентифицировать пользователя на сервере по его socketThread? socketThread по архитектуре приложения не должен знать вообще ничего про чат. Как нам в таком случае серверу сообщить это? Через передачу какой-то системной строки (/nick etc) - странно. Наверное нужно написать адаптер для DataInputStream/DataOutputStream, чтобы была возможность передавать объекты. Но тогда нужно сериализацию делать и тд. Или я не в том направлению думаю? (Пока что вывожу имя SocketThread как имя пользователя от которого было сообщение)
2) В socketThread есть бесконечный цикл который ожидает сообщение ( in.readUTF(); ). Если клиент отключается от сервера, когда, например, мы делаем socketThread.stop(), тогда метод ожидающий сообщение упадет с исключением. Как это предотвратить? Сделать также как и в ServerSocketThread, некий таймаут?
3) Зачем в интерфейсе ServerSocketThreadListener методы имеют входящий параметр ServerSocketThread. Ведь все классы которые реализуют этот интерфейс у нас уже итак хранят этот параметр. (ServerSocketThread server, файл ChatServer.java)
4) Зачем при таймауте server.accepted() у нас вызывается метод onServerException, видимо предполагалось вызвать метод onServerTimeout()?

Вопрос по IP и номеру порта. Откуда брать эти цифры? Есть какие-то рекомендации по выбору порта?
Как корректно добавлять все файлы чата на github, чтобы не потерялась структура папок/проектов?

1. Почему метод putLog выносится в отдельный поток?
Расскажите еще раз, пожалуйста, более подробно об invokeLater, в какой момент выполняются методы внутри run.
2. Почему "полусокет" описывается только со стороны клиента, а со стороны сервера не описывается?
3. Мы запустили сервер, залогинились. После этого выключили сервер (stop).
Почему при вводе сообщений в ClientGui все равно приходит эхо с сервера, хотя он по идеи выключен?

1)Что выполняет "out.flush();"?
2)Немного запутался, для чего нужен метод "putLog". Что в нем происходит?
3)В "DateFormat" мы сами указываем формат времени, в котором оно потом будет представлено? Т.е.
можно указать например:"HH:mm" или "HH"?
4)IP который у нас указан - это наш IP? С которым мы идем к серверу?
5)Можем ли мы постучаться в любой свободный порт или только в определённый, который мы пропишем?

1 метод server.setSoTimeout(timeout); прерывает только .accpet() ? он не может прервать .read() в потоках?
2 на видео 1 час 14 мин, говорится, что генерируется сокет от сервера, потом ему дается имя curentClient и идет информация, что вызываются потоки клиента? Это полусокет сервара или клиента, или когда они объединятся они становятся какой то одной единицей?
3 В клинете нажал несколько раз login, не очень понял, что происходит? перезаписывается текущий сокет клиента и создаются дополнительные сокеты сервера? А что происходит со связью уже нетекущих сокетов клиента и сокетов сервара, она где то висит? но используется последняя связь?
4 127.0.0.1 у сервара это какое то значение по-умолчанию записывается? не нашел где мы его передаем этот адрес серваку, клиенту передаем? или он еще что то означает?
5 один обработчик на большое количество сокетов синхронизированный, т.е все сокеты встают в очередь? это как то избежать можно используя один сервер?
6 Общее понимание для исправления: запускается сервер, создается объект серверсокет, которой с помощью метода ожидает .accept() ожидает подключения клиента, на клиенте создается сокет с адресом подключения и портом. Этот сокет ищет (где ищет? как он определяет область поиска?) данный адрес и порт (можно у одного сервера открыть несоклько портов для подключения или это будет два разных сервака?). Метод accept() получает сокет клиента и генерирует сокет сервера с тем же адресом, но другим портом. (этот порт генерируется случайно с поиском свободных у данного сервара? чтобы подключатся к нему?). Формируется связь данных сокетов (не понял про связь, она где то внутри формируется и мы не можем в java её увидеть? или сохранить, как генерируемые сокеты знают что они взаимосвязаны с течением времени?). Связь в нашем примере основана на потоках ввода и вывода, out серверного сокета отрправляет поток данных на сокет клиента он с помощью in его принимает и наоборот.

1 Подробнее про System.getOutputStream() .getInputStream(). В лекции Вы сказал, что клиент "дает нам свои потоки ввода-вывода" - это как?
2 Не понял смысл фразу "Полусокет"
3 Внутрення реализация того как java запускает сервер на локалхосте(тот момент когда мы передаем локальный ip в конструктор)?
4 Правильное(грамотное, хороший тон, как надо и когда и тд) формирование пакетов и модулей
5 Схема хорошая, но хочется еще подробнее(аналогии)
6 Внутренности Sockect, аналоги - что это простоми словами
7 Объясните, пожалуйста, фишку с таймаутом. Сервер отключает клента через время, только потому что он сам не может выйти? Зачем?
8 Не понимаю разницу между SocketThread и ServerSocketThread. Первый для создания сокетов сервера и клиента, а второй?

Очень наглядно. В связи с этим вопрос на два урока вперед - что мне в портфолио приложить? Попробовать написать свой чат?

почему клиент гуи никак не реагирует у нас на стоп сервера?

- В классе ServerSocketThread не могу понять, почему server.setSoTimeout() находится не в цикле while. Посмотрел разные источники нигде не написано почему срабатывает timeout.
- Вопрос по socket-ам. Если произошёл разрыв соединения, то как полусокеты (на своих сторанах) узнают об этом? Кидают IOException? Просто если (в текущем варианте проекта chat) остановить ChatServer, то ChatClient узнает от разрыве только когда попробует отправить сообщение.
- Почему из ClientGUI не вынести обработку кнопок и полей в отдельный класс? Как это сделано у ChatServer и ServerGUI. А то "простыня" большая получается.

1. Что произойдет если 2 раза попытаться залогиниться (если не убрать шторку),
 будет уже 2 сокеттрида? мы ж ограничение на коннект не делали.
 2. Если сможет так создать 2 сокеттрида, то что будет с предыдущим?
 3. Сокеттрид, ссылка на который потеряна, закроется после завершения ран?
 4. Завершится ли вобще ран в сокеттриде учитывая условие вайл неинтерраптед,
 он ведь по факту не закрыт?
 5. Сможет ли его удалить сборщик мусора если он сам не закроется?
 6. Как этот чат сделать доступным в интернете все таки, достаточно ли
 указать серверу реальный айпи вместо локалхоста, и клиенту его же для подключения?

1)Я не понимаю, что за Exception (-ы) в компиляторе постоянно бегут, хотя понимаю, но не до конца, если можно, то хорошо бы подробно объяснить, как для чайников
2. Не до конца понимаю, как работают зависимости между модулями
Не, я понимаю, что сервер знает Нетворк, клиент знает нетворк, друг о друге они нечего не знают и это круто, но как работает эта зависимость (в контексте с модулями)
3. С пониманием сокетов беда . . . Не понимаю что это такое ... Определение знаю. . .
4. Если можно, то проговорите логику всего кода, кратко, что от куда куда, как потоки/сокеты/да всё что там есть подвижное себя ведёт
Я понимаю элементы в отдельности, но в целом не могу всё осознать
5. В коде, в SocketTread не понимаю, кто такой listener, что он делает с точки зрения синтаксиса java - понимаю, а для чего он так необходим и что это . . . ?
6. Мне не до конца понятно, почему у нас 2 метода с исключениями : showException и uncaughException ?
7. SocketThread - это обобщенный класс - поток у нас в коде, т.е. и для клиента и для сервера

Хотелось бы в кратце, если это возможно, полную цепочку событий с момента Нажатия на кнопку Start (именно создание Server socket и так далее) . То есть, не вдаваясь в подробности кода (там все понятно), описать что , откуда и в каком методе берется и как взаимодействует

Каким способом можно управлять большим колличеством потоков, какими способами? например если большой сервис, интересна общая концепция хотя бы.
* */