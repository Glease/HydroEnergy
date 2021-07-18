# HydroEnergy

Screenshots from hydroelectric dams always fascinated me. Majestic structures that fit on an industrial base in style. Sadly, no mod was ever able to incorporate these structures into tech-heavy mod packs. I always felt that packs like [GregTech: New Horizons 2](https://www.curseforge.com/minecraft/modpacks/gt-new-horizons) were missing this kind of feature. This mod tackles this challenge and provides a proof of concept for energy storage in hydroelectric dams.

>>Nerd Talk Alert<<
If you are interested the technical realization of this mod, [click here](https://github.com/SinTh0r4s/HydroEnergy/blob/master/docs/overview.md) for a high level concept summary.
>>End Nerd Talk Alert<<


Features: (Screenshots!)
* Flood suited landscapes for massive EU storage! And i mean massive. The only limit is whatever the admin allows you ;-)
* MyTown2 support for server deployment!
* Gregified!
* Minimalistic server load per dam: comparable to a battery buffer!
* Upgrade the generator and pump!
* Visual debug mode to show the area of the dam and allow you easy work on "leaks"/terraforming in survival mode!
* Watch ruins emerge from your lake! Just to see them vanish again once you generated some more power!
* Rain is your friend ;-)!

Known issues:
- Apple dropped major OpenGL support. Rendering is not quite possible on new Mac's. There is a backup in place, but you will have to deal with things looking not quite right.
- Shaders won't look right and there is no easy fix. But it will definitively not crash the game.
- A [list of tolerated issues](https://github.com/SinTh0r4s/HydroEnergy/issues/16) for now.


Gameplay Guide

0. Find a nice valley or another suitable area. (Optionally flood a village or your unused early game base for some spectacular visuals). The deeper and larger the lake, the more energy you can store. The only limit is your imagination (and an admin with a config)

1. Craft up a HydroDam, Fluid Input Hatch, Fluid Output hatch and tons of light concrete.

2. Build a 5x5x5, hollow concrete box with an open back. Place the controller in the front. The lake will spawn and spread from the center of the concrete box.

3. Build your dam. Make it grand. And shiny. Make it GregTastic!

4. Define your bounding box(WIKIPEDIA LINK). You will need to tell the dam how far the lake can spread in all 4 directions (North, East, South, West)* as well as up and down. You can compare it to an Ender Quarry from Extra Utilities or BuildCraft. Just instead of torches to mark the corners you will have to enter the distance in a GUI. Just walk to the edges of your soon-to-be lake and grab the coordinates with F3 (screenshot!)

The lake will act like vanilla water: consume plants like grass, but not solid blocks like trees, dirt or stone.

Got your bounding box? Let's continue!

5. Take a screwdriver and right-click the HydroDam controller. Adjust the values to fit your bounding box. If you cannot change the values any further you might switched up the direction or hit the limit of your (server) config.

6. Spread the water! (SCREENSHOT) Use the debug mode to see the spreading progress. In the beginning your dam will be empty so you wouldn't see much of a change. Debug mode will show you where water will be stored if you provide enough energy and allow you to walk normally as well as breath on the lakes bottom. Always. You need to keep the chunks loaded for the spreading to happen. Afterwards you don't need them loaded to store water. In case the spreading stopped you can trigger a block update on the border of the water (not HydroDam Controller!) that you want to keep spreading. Water will never spread beyond the limits you configured for your bounding box and also never into cleaned chunks (MyTown2) where you don't have any building rights.
Tipp: Don't worry about your machines if you did a mistake. GregTech machines won't explode from water. Only rain. But you still have a good chance to mess up your base ;-)


* *N*obody *E*njoys *S*oviet *W*omble