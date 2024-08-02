
*How much damage does your weapon dish out?*
_ _ _
Weaponmark is a command line utility to compare weapon efficiencies for the game: **Mage The Ascension (WW4600 - Revised Edition)**.
It  works for martial arts, brawling, melee, and firearm weapons. You supply the inputs like weapon name, hit dice, etc. and Weaponmark will show you the average hits and damage per turn as well as the percent of time you will miss, botch, or hit but do no damage.
Weaponmark is easy to use. You don't need to learn the command line parameters first, just leave them off and the program will ask you for the inputs.

## Quick Example:
A fist fighter with 4 Dexterity and 4 in the skill *Brawl* punches with 8 hit dice. She also has 4 Strength; this becomes her Damage Dice.

Here is what that looks like in Weaponmark:

![Punch Example](https://raw.githubusercontent.com/locke8/weaponmark/master/src/main/resources/screens/punch.png)

Since we don't yet know the command line parameters, we type `weaponmark [ENTER]` to have the program ask us for them.
We enter the first three parameters (Weapon Name, Hit Dice, and Damage Dice) and accept the defaults for the rest (by pressing enter repeatedly).
Next we see the line `= weaponmark Punch 8 4` which shows us how we can run the same command by supplying the needed parameters on the command line, teaching us as we go.
Lastly, weaponmark prints the details of how the benchmark was run and what the results were.

Here is the exact same command entered *with parameters* on the command line:

![Punch Example](https://raw.githubusercontent.com/locke8/weaponmark/master/src/main/resources/screens/punch2.png)

## Motivation
The purpose of this utility is to aid in play-balancing the game.
If you have played *Mage The Ascension* you know it is both wonderful and wonderfully vague in explaining the rule system.
Weaponmark enables the storyteller to experiment with rule changes (house rules) and see what the actual effects will be on play balance.

It is also useful to players looking to compare the strengths and weaknesses of various attacks (e.g. a punch vs. a rifle shot) and their effectiveness on opponents who can soak damage. 

Questions such as, *"how much damage would my attack do against an opponent with 3 stamina and 2 armor?"*, or, *"how likely am I to miss or botch if I split my action into three Multi-Actions?"*,
 can all be answered.
### Another Example - The Broken Kick Attack
When I started playing my martial artist character I was dismayed to find a kick was only a +1 to damage dice at a cost of +1 to hit difficulty (Core Rules p.241)...
I suspected the kick-attack was a loser.

Weaponmark reveals the truth:

![Broken Kick Example](https://raw.githubusercontent.com/locke8/weaponmark/master/src/main/resources/screens/kicking.png)

As you can see the basic *Kick* attack is indeed broken:

In exchange for being four times more likely to botch and being ineffective 1 in every 6 attempts (as opposed to 1 in 10 for a strike/punch) -- *you do slightly **less** damage per attempt than striking!*
There is no reason to ever *Kick* using the core rules, *Strike* instead - you get slightly more damage on average with a lot less risk of botching or missing.

#### Possible Solution - Change the Damage Bonus to +2 or +3
As a potential fix, you might try this formula when kicking:

  - Damage Dice = Strength + 3 *(compared to +1 in rules)*

  - Hit Difficulty = 7 *(no change from core rules)*

This seems to yield results with a reasonable upside.
You will still botch four times as often and be ineffective 1 in 6.7 attempts -- BUT -- you will also average +0.7 to +0.8 more points of damage when your attack is successful.
If you would like the bonus to damage to be less (about +0.3 points) use Strength + 2 instead.
For me, an additional ten percent damage in exchange for the increased botching and missing is not compelling but the possibility of a twenty five percent increase makes the Kick a viable option from a risk/reward standpoint.
Each Storyteller should decide what works best for their particular campaign (I seriously doubt my own storyteller would agree to +3 damage).

Weaponmark can help you understand what is going on with an attack, uncover hidden problems like the one above, and make it easy to experiment with alternative solutions.

## Getting Started
### Installation
#### Windows
Installation consists of downloading the program and unzipping it to a folder on your computer.
You will also need to install Java JRE 8 if you do not already have it.

**Step 1:** Install Java JRE 8 if not already installed on your computer:
> Download page: http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html -- you want the file whose name ends with `windows-x64.exe`
>
> Detailed instructions: https://docs.oracle.com/javase/8/docs/technotes/guides/install/windows_jre_install.html

**Step 2:** Download the utility and unzip it to a folder on your computer:
> Download [this](https://github.com/locke8/weaponmark/releases/download/1.1-BETA/weaponmark_1.1b.zip) zip file
>
> Create a folder in `C:\Program Files` called `Weaponmark`
>
> Unzip the downloaded file into `C:\Program Files\Weaponmark`

### Usage
1. Start a command prompt:
> Click on the Start menu and type: `cmd.exe [ENTER]`

2. Switch to the program directory:
> Type: `cd \Program Files\Weaponnmark [ENTER]`

3. Run the program:
> Type: `weaponmark [ENTER]`

#### Examples
##### Usage Examples
Type `weaponmark --examples [ENTER]` or `weaponmark -e [ENTER]` to show some examples with explanations:

![Examples Screen](https://raw.githubusercontent.com/locke8/weaponmark/master/src/main/resources/screens/examples.png)

#### Interactive Mode
Type `weaponmark [ENTER]' or 'weaponmark --interactive [ENTER]` or `weaponmark -i [ENTER]` to receive prompts for all needed input:

![Interactive-mode Screen](https://raw.githubusercontent.com/locke8/weaponmark/master/src/main/resources/screens/interactive.png)
This also displays what the command line would look like had you entered the parameters directly. Study these and you will see how to run benchmarks more quickly.

#### Help
Type `weaponmark -h [ENTER]` or `weaponmark --help [ENTER]` to view usage instructions and options:

![Help Screen](https://raw.githubusercontent.com/locke8/weaponmark/master/src/main/resources/screens/help.png)

## More Features
### Actions
Use the `-a<#>` parameter to specify the number of actions per turn. The default value is 1. For example, a character with Distort Time(1) would gain a free action every turn and therefore warrant an `-a2` parameter on the command line.

*core rules p.213-216*
### Multiple Actions
You can specify that the turn include a multi-action with `-m<#>`. A Multi-action action allows for 2 or 3 usages of the weapon during a single action. These "split" actions suffer from dice pool penalties. By default, these penalties are 2, 3, 4 for usage 1, 2, and (optionally) 3 in a multi-action. The penalties are subtracted from the hit dice pool when executing the attack. The penalty values can be overridden with the `-x<#>`, `-y<#>`, and `-z<#>` parameters.

*core rules p.215, p.239*
### Soak Dice
If you would like to see how your weapon will perform against opponents who can soak damage, use the `-s<#>` parameter.
For example, if you want to test your weapon effectiveness against an opponent with three soak dice, include `-s3` on the command line.

You can also override the standard hit difficulty (6) by using the `soakDifficulty` parameter.
For example, when playing with "Cinematic Rules" lethal damage can be soaked at a difficulty of 8 so we would add `-d8` or `--soakDifficulty 8` to the command line.

*core rules p.238*
### Specialties
Specialties, as described on page 117 of the core rules book, are supported. If you are specialized in the use of your weapon, every 10 you roll to hit yields a bonus roll. Additional rolls of 10 extend this effect. Rolling a 1 on the bonus roll cancels out the 10. Include the `-l` parameter to denote specialization with a weapon.

*core rules p.117*
### KiloTurns
If you are not satisfied with the default 200,000 turns used to determine benchmark results, you can override this. Use `-t<#>` to specify the number of turns (in thousands) that the benchmark should run.  For example, to run one million turns, include `-t1000` on the command line. Higher values will increase accuracy slightly.
### Botching
The core rules do not provide definitive instructions for handling the consequences of a botch.
Obviously the player's current action fails but they may also lose one or more additional future actions.
At the discretion of the Storyteller, the player may lose their next action, or all actions remaining in the current turn, or that plus the first action of the subsequent turn.
The weapon may jam or break, you may harm another teammate, etc.

In Weaponmark a botch forfeits any and all actions remaining in the current turn.
### About
Typing: `weaponmark --about [ENTER]` will open your default browser and display this readme file.
## License
This project is licensed under the MIT License - see the [LICENSE.txt](https://raw.githubusercontent.com/locke8/weaponmark/master/LICENSE.txt) file for details.

## Acknowledgements
Special thanks to the creators of [Scallop](https://github.com/scallop/scallop), [sbt-BuildInfo](https://github.com/sbt/sbt-buildinfo), [JCDP](https://github.com/dialex/JCDP), [sbt-assembly](https://github.com/sbt/sbt-assembly), and [ScalaTest](http://www.scalatest.org/) without which Weaponmark would not exist.

Eternal gratitude to [Martin Ordersky](https://en.wikipedia.org/wiki/Martin_Odersky) and the folks at [Lightbend](http://www.lightbend.com/).
[Functional Programming](https://www.coursera.org/learn/progfun1), [Actor-based Systems](http://akka.io/), [The Reactive Manifesto](http://www.reactivemanifesto.org/): not since Smalltalk have I enjoyed development this much - I am forever in your debt.
