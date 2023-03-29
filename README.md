<h1 align="center">
    <a href="https://discord.com/api/oauth2/authorize?client_id=926821629324046407&permissions=8&scope=bot%20applications.commands">
        <img src="https://cdn.discordapp.com/attachments/1075443722512244786/1090412896909008996/resized.png">
    </a>
  <br>
    Ganyu
  <br>
 </h1>
<h4 align="center">A simple discord bot written using JDA.</h4>

## Requirements
- JDK 11+
- A mongoDB database

## Installation

1. Set up a MongoDB database. You may install a [local database](https://www.mongodb.com/docs/manual/administration/install-community/) or have a cloud database using [MongoDB Atlas](https://www.mongodb.com/atlas/database).
2. Download the latest release from Releases.
3. Place JAR into an empty directory.
4. Run the JAR file in terminal:
```bash
java -jar GanyuBot.jar
```
5. The application will quit out. Fill in the generated config.cfg. For example:
```
STATUS:nice
PREFIX:>r
TOKEN:<YOUR BOT TOKEN HERE>
DB_URI:<YOUR DB URI HERE>
```
6. Run the bot again.
