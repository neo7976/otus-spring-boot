###Как проверить вручную
#Запустите приложение, затем получите токен администратора:
```shell
TOKEN=$(curl -s -H 'Content-Type: application/json' -d '{"username":"admin","password":"password"}' localhost:9000/api/auth/login | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')
```

```shell
curl -s -H "Authorization: Bearer $TOKEN" localhost:9000/actuator/health
```

Health с подробностями, включая library:
```shell
curl -s -H "Authorization: Bearer $TOKEN" localhost:9000/actuator/health
```

Своя метрика:
```shell
curl -s -H "Authorization: Bearer $TOKEN" localhost:9000/actuator/metrics/library.books.count
```

Лог-файл:
```shell
curl -s -H "Authorization: Bearer $TOKEN" localhost:9000/actuator/logfile | tail
```
