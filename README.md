<a href='https://docshoster.org/p/jjeeff248/chaps-challenge/latest/introduction.html' target='_blank'>
  <img src='https://docshoster.org/pstatic/jjeeff248/chaps-challenge/latest/badge.svg'/>
</a>

# Chaps-challenge

1. [Setup and Run](https://gitlab.ecs.vuw.ac.nz/course-work/swen225/2022/project1/t3/chaps-challenge#setting-up-and-running-the-game)
2. [About](https://gitlab.ecs.vuw.ac.nz/course-work/swen225/2022/project1/t3/chaps-challenge#about)
3. [Warnings](https://gitlab.ecs.vuw.ac.nz/course-work/swen225/2022/project1/t3/chaps-challenge#warnings)
4. [Gource](https://gitlab.ecs.vuw.ac.nz/course-work/swen225/2022/project1/t3/chaps-challenge#gource)

## Setting up and running the game

We recommend using Intellij to run our game as that is where most of the development took place. You can also use VSCode if you have the required Java extensions.  
Our program has been tested on the ECS machines using Intellij and is known to work well.

**Set up**
1. Launch Intellij  
2. Select `File > Open` or `Projects > Open` and then locate our project folder  
3. You can try running the program now (See below)  
4. Additional setup could include  
    a) Right-clicking the resource folder and selecting `Mark directory as > Resources Root`  
    b) Setting the `src/main` folder as the `Source Root` directory  
    c) Setting the `src/test` folder as the `Test Source Root` directory  
    d) Add the `resources/levels/level2.jar` to the dependencies. Right-click the jar and select `Add as Library...` then hit OK on the popup  

**Running the game**
1. Check that the `Main` run option in the top right dropdown in Intellij is selected. ![Screenshot of Main selected in the dropdown](https://cdn.discordapp.com/attachments/863945726492540979/1030387855467495475/unknown.png)
2. Click the green play arrow next to the drop-down to run the game.


**If the above fails**
1. Select `Run Current File` in the same drop down
2. Locate and open `src/nz/ac/vuw/ecs/swen225/gp22/app/Main.java`
3. Run the program using the play button next to the drop-down.


## About

Our game is candy land themed. The gingerbread chap player needs to collect all the candy jar treasures by collecting lollipop keys to unlock all the doors before he can make it through the liquorice gate. 

We implemented all required key commands, as well as one extra Ctrl+Q to quick, save the game (and game recording) without exiting. 

Our Recorder can be accessed through the Home Menu under "Replay Game". You can replay the game forward and backwards. Speed is via the button that is next to the play/pause button. 

When running Fuzz tests on level 2, our Chap tends to die very quickly. However, you can use the menubar (New > Level 2) (Or use the key command, Ctrl+2) while the Fuzz test is still running to re-run Level 2 and keep testing inputs. This allows you to test level 2 more thoroughly. 

## Warnings

We had some issues with our game sound, so the volume is quite loud (especially the item pick-up sounds). Please be warned, and **turn down your volume!**

When running Fuzz tests on level 1, if Chap manages to win the level, a pop-up will appear. **DO NOT CLICK OFF THE WINDOW**. You do not need to click anything, and if you do click off the window the robot key presses will mess with your other programs. Level 2 will automatically start upon test 1 completion. 


## Gource

Here is our Gource.io video https://youtu.be/88qOIl1HkNU. It doesn't include the Javadoc commits and a few other smaller commits completed close to the deadline but the majority of our progress is in the video.
