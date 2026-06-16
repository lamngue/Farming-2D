# 🌱 Farming Game (Java + LibGDX)

A simple 2D farming game built with **Java** and **LibGDX** as a personal project to learn game development fundamentals, including:

* Game loops
* Sprite rendering
* Tile-based worlds
* Camera systems
* Inventory management
* Crop growth mechanics
* World transitions
* Shop systems
* Save/Load functionality

---

## 📸 Screenshots

### Main Menu

<img width="646" height="514" alt="Main Menu" src="https://github.com/user-attachments/assets/f72d51c7-26bc-48cb-a814-a911af8c95d6" />

### Farming

Plant, water, and harvest crops through multiple growth stages.

<img width="639" height="508" alt="Farming" src="https://github.com/user-attachments/assets/360393bb-e30c-4a27-a2e2-4b87be3dc725" />

<img width="640" height="514" alt="Crop Growth" src="https://github.com/user-attachments/assets/e3cda948-e204-473f-a98b-1d4b6f29697b" />

### Shop System

Buy seeds and sell harvested crops.

<img width="643" height="511" alt="Shop Buy" src="https://github.com/user-attachments/assets/f6f7346d-8a9c-4e4f-8013-2ea599886eee" />

<img width="639" height="513" alt="Shop Sell" src="https://github.com/user-attachments/assets/a7081d93-1ec2-480f-9364-cddc893c1d44" />

---

## 🎮 Controls

### Player Movement

| Key | Action     |
| --- | ---------- |
| W   | Move Up    |
| A   | Move Left  |
| S   | Move Down  |
| D   | Move Right |

### Farming Actions

| Key | Action             |
| --- | ------------------ |
| 1-4 | Select Seed Type   |
| Q   | Plant Seed         |
| E   | Water Crop         |
| H   | Harvest Crop       |
| R   | Remove Wilted Crop |

### Shop Controls

| Key   | Action                           |
| ----- | -------------------------------- |
| TAB   | Switch between BUY and SELL tabs |
| W / S | Navigate item list               |
| A / D | Adjust quantity                  |
| ENTER | Confirm purchase/sale            |
| ESC   | Close shop                       |

---

## 🌾 Current Features

### Farming

* Multiple crop types:

  * Wheat
  * Tomato
  * Potato
  * Pumpkin

* Multi-stage crop growth

* Watering system

* Harvesting system

* Crop inventory storage

### Economy

* Buy seeds from the shop
* Sell harvested crops
* Money system
* Quantity-based transactions

### World System

* Farm area
* Green Field area
* World transitions via map exits
* Camera follow system

### Save & Load

* Save game support
* Load previous progress

---

## 🛠 Built With

* Java
* LibGDX
* LWJGL3
* Gradle

---

## 🚀 Running the Project

Clone the repository:

```bash
git clone https://github.com/YOUR_USERNAME/FarmingGame.git
cd FarmingGame
```

Run the desktop version:

```bash
./gradlew lwjgl3:run
```

Build a runnable JAR:

```bash
./gradlew lwjgl3:jar
```

The generated JAR can be found in:

```text
lwjgl3/build/libs/
```

---

## 📁 Project Structure

```text
FarmingGame
├── core/        # Shared game logic
├── lwjgl3/      # Desktop launcher
├── assets/      # Sprites, textures, atlases
└── saves/       # Save data
```

---

## 🎯 Learning Goals

This project was primarily created to learn:

* Object-oriented game architecture
* Entity management
* Input handling
* Sprite animation
* Save/load systems
* UI design in LibGDX
* Game state management

---

## 🔮 Planned Features

* Animals / livestock
* Expanded shop inventory
* Tool upgrades
* More crop varieties
* Day/night cycle
* NPC interactions
* Improved UI
* Sound effects and music

---

## License

This project is for educational and portfolio purposes.
