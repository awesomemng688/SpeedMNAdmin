# Match #1 (27019) Серверийн "Nested Screen" алдааг засах төлөвлөгөө

Таны `_start_server` скрипт дотор өөрөө `screen -dmS` команд байгаа нь `systemd` үйлчилгээтэй зөрчилдөж, маш олон "Dead" (үхмэл) сессүүд үүсгэж байна. Үүнийг нэг мөсөн шийдэх хамгийн найдвартай арга бол үйлчилгээний файлыг шууд тоглоом асаах команд руу нь зааж өгөх юм.

## Оношлогоо
- **Скрипт**: `screen -dmS 27019 ./hlds_run20 ...` гэж байна.
- **Үйлчилгээ**: Дахин `screen` нээх гэж оролдож байгаа нь "Screen inside Screen" алдаа үүсгэж, систем процессыг "Dead" гэж үзээд байна.

## Санал болгож буй засах үйлдлүүд (SSH Терминал дээр)

Дараах алхмуудыг дэс дарааллаар нь ажиллуулаарай:

### 1. Гацсан бүх сессүүдийг нэг удаа бүрэн цэвэрлэх
```bash
sudo screen -wipe
sudo pkill -9 hlds
```

### 2. Match серверийн (27019) үйлчилгээг "Direct" болгож шинэчлэх
Бид скриптийг алгасаж, шууд тоглоомын командыг `service` файл дотор бичиж өгнө. Ингэснээр ямар нэг зөрчил гарахгүй:

```bash
sudo tee /etc/systemd/system/hlds_9.service <<EOF
[Unit]
Description=SpeedMN CS 1.6 Match Server 27019
After=network.target

[Service]
Type=forking
User=root
WorkingDirectory=/home/match1/27019
# Скриптийг биш, шууд тоглоомын командыг Screen-ээр асаана
ExecStart=/usr/bin/screen -dmS 27019 ./hlds_run20 -game cstrike -secure +map de_dust2 +ip 203.34.37.57 +port 27019 +maxplayers 12 +sys_ticrate 1000 -pingboost 3 +exec server.cfg
ExecStop=/usr/bin/screen -S 27019 -X quit
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

# Тохиргоог идэвхжүүлж асаах
sudo systemctl daemon-reload
sudo systemctl restart hlds_9
```

## Шалгах төлөвлөгөө
1. `sudo systemctl status hlds_9` гэж бичихэд "active (running)" гэсэн ногоон бичиг гарах.
2. `screen -r 27019` гэж бичихэд тоглоомын консол шууд харагдах.
3. Апп дээр тухайн сервер "LIVE NOW" болох.

---

> [!TIP]
> Энэ арга нь скрипт ашигласнаас хамаагүй найдвартай бөгөөд Линукс систем серверийг чинь илүү сайн хянаж чадна.

Би эдгээр командыг ажиллуулахад бэлэн үү?
