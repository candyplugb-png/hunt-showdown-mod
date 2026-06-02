# 🎯 Hunt Showdown Dark Sight — Minecraft 1.20.1 Forge Mod

Воссоздаёт механику **Тёмного Зрения** из **Hunt Showdown 1896** в Minecraft 1.20.1.

---

## Возможности

### 🥚 Яйцо Тёмного Гнезда (`dark_nest_egg`)
- Получить: `/give @s huntmod:dark_nest_egg`
- Нажать ПКМ на блок — разместит Тёмное Гнездо

### 🪺 Тёмное Гнездо (`dark_nest`)
- Нажать **ПКМ** на блок — выдаёт **Тёмное Зрение** игроку
- Максимум **2 разных игрока** могут получить Тёмное Зрение от одного гнезда
- Блок светится при активации Тёмного Зрения

### 👁️ Тёмное Зрение (Dark Sight)
- Удержание кнопки **Z** (настраивается в Управлении → Hunt Showdown):
  - Экран темнеет (эффект как у Хранителя/Warden)
  - Тёмное Гнездо **подсвечивается жёлтым на любом расстоянии**
  - Если у игрока есть Тёмное Зрение — **враги в радиусе 75 блоков подсвечиваются красной обводкой**

---

## Установка

1. Установи [Minecraft Forge 1.20.1](https://files.minecraftforge.net/)
2. Скачай последний `.jar` из [Releases](../../releases)
3. Помести в папку `mods/`
4. Запусти Minecraft с профилем Forge 1.20.1

---

## Сборка из исходников

```bash
git clone https://github.com/yourname/hunt-showdown-mod.git
cd hunt-showdown-mod

# Windows:
gradlew.bat genIntellijRuns
gradlew.bat build

# Linux/Mac:
./gradlew genIntellijRuns
./gradlew build
```

JAR будет в папке `build/libs/`.

---

## Команды

| Команда | Действие |
|--------|---------|
| `/give @s huntmod:dark_nest_egg` | Получить яйцо |
| `/give @s huntmod:dark_nest` | Получить блок напрямую |
| `/effect give @s huntmod:dark_sight 99999` | Выдать эффект вручную |

---

## Текстуры

Текстуры нужно создать самостоятельно или скачать:
- `assets/huntmod/textures/block/dark_nest.png` — 16×16
- `assets/huntmod/textures/item/dark_nest_egg.png` — 16×16  
- `assets/huntmod/textures/mob_effect/dark_sight.png` — 18×18

---

## Лицензия

MIT — используй свободно.
