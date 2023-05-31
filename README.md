# DyingRabbit99
A Minecraft Paper Plugin

## What is DyingRabbit99
DyingRabbit is a hobby project of mine. It´s a Minecraft Paper Plugin I´m developing for a private survival server. I use GitHub primarily for organizational reasons, but everyone is welcome to use it, suggest new features or contribute.

This plugin is meant to make your survival game a little easier, without removing intended challenges.
Please note, that all the text you find in this plugin is German (my first language). An option to switch the language to English will probably be implemented in the future.

### Current features
 - Config: Edit the plugin´s options by accessing the config.yml file or by using /config.
 - Find: Retrieve the coordinates of any player who is currently online.
 - Locations: Save any location in your world and access it to find back to your home, nether portal, giant creeper farm or the diamonds you left behind, because you forgot your fortune pickaxe.
 - Messages: Configure individual messages to be sent at certain events.
   - OnPlayerJoinMessage: Configure a welcome message that is always sent to a player when he joins.
   - PrintDeathCords: Printing your death coordinates to the chat when you die, to help you to get your stuff back in time.
 - Portal: Simulate Portals and calculate your portals destination.
 - More features coming soon...

### Planned features
 - Option to make several helpful calculations with your locations. (E.G. How big is that village radius; etc.) (Things you could search on the minecraft wiki for to then type complex formulas into your calculator)
 - Command to look up crafting recipes.
 - Command to look up the current location of other players. (With privacy settings)
 - Adding a language option and adding English as language.

Note that all these planned features are not necessarily already in development. Usually you can assume that only features, you can find an open branch about, are currently in development. You are welcome to contribute to this project by implementing other features you find on this list.

## How does this GitHub repository work
The default branch "dev" is the current state of development.
New major implementations are developed in a separate branch (usually called "feature/FEATURENAME"). When they are finished, that branch is merged into "dev".
When "dev" is in a stable state and includes considerable improvements to the latest release, it will be released.
Upon release a new branch is created with the release version as branch name. Also, the latest branch is updated to this new released version as well. These branches will also include a compiled DyingRabbit-VERSION.jar file, you can simply copy into your server´s plugin folder to start using this plugin.
