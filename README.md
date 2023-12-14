# DyingRabbit99
A Minecraft Paper Plugin

DyingRabbit99 version INDEV-1.2.2 and above is made for Minecraft Java 1.20.4

## What is DyingRabbit99
DyingRabbit is a hobby project of mine. It´s a Minecraft Paper Plugin I´m developing for a private survival server. I use GitHub primarily for organizational reasons, but everyone is welcome to use it, suggest new features or contribute.

This plugin is meant to make your survival game a little easier, without removing intended challenges.
Please note, that any text you find in this plugin is German (my first language). An option to switch to English language will probably be implemented in the future.

### Current features
 - Config: Edit the plugin´s options by accessing the config.yml file or by using /config.
 - Find: Look up the coordinates of any player who is currently online.
 - Locations: Save any location in your world and access it to find back to your home, nether portal, farm or the diamonds you left behind, because you forgot your fortune pickaxe.
 - Messages: Configure individual messages to be sent at certain events.
   - OnPlayerJoinMessage: Configure a welcome message that is always sent to a player when he joins.
   - PrintDeathCords: Printing your death coordinates to the chat when you die, to help you to get your stuff back in time.
 - Portal: Simulate nether portals and calculate your portals destination.
 - Menu: Open a menu to handle many of this plugin's features using a graphical UI.
 - More features coming soon...

### Planned features
 - Option to make several helpful calculations with your locations. (E.G. How big is that village radius; etc.) (Things you could search on the minecraft wiki for to then type complex formulas into your calculator)
 - Command to help you build circles, spheres and other shapes.
 - Adding a language option and adding English as language.

Note that all these planned features are not necessarily already in development. Usually you can assume that only features, you can find an open branch about, are currently in development. You are welcome to contribute to this project by implementing other features you find on this list.

## How does this GitHub repository work
The default branch "dev" is the current state of development.
New major implementations are developed in a separate branch (usually called "feature/FEATURENAME"). When they are finished, that branch is merged into "dev".
When "dev" is in a stable state and includes considerable improvements to the latest release, it will be released.
Every release comes with a compiled DyingRabbit-VERSION.jar file, you can simply copy into your server´s plugin folder to start using this plugin.


## Backwards compatability
From version INDEV-1.2.0, I´ll try to ensure backwards compatability and obviate data loss upon updating your server.
Nevertheless, I can´t guarantee it, so please make sure to always back up your data before updating this plugin!