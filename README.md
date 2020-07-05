# Tank Battle
Simple game for two players on one device.

Rules:
- Each player has 5 tanks. One of player's tank is leader one. Only leader tank can shoot and it cannot be shoot until there are others tank unshooted.
- On new game start it's randomly chosen whose player's turn is. After that each player makes his shoot in turn until game is over.
- Player wins when he hits enemy's leader tank.
- Player can adjust angle of shooting tapping around his leader tank. He can adjust the strength of the shooting as well.
- For now game available only in landscape orientation.

Minimal SDK: 16

Used technologies:
- There is only one activity with a Custom view as a battle field. There are no animations used, just a painting on canvas (circles and bitmaps)
