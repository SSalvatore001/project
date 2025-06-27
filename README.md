# 3D Game Programming Course Code

Here you can find the source code used in the 3D Game Programming course. There
are

1. the example programs of the _jMonkeyEngine Beginner Tutorials_
   (https://wiki.jmonkeyengine.org/docs/3.4/tutorials/beginner/beginner.html) and

2. the example game *Droids*.

The source consists of the following Gradle subprojects:

* _:jme-helloworld_
* _:droids:app_
* _:droids:model_
* _:droids:json_
* _:common_
* _:jme-common_

The subproject _:jme-helloworld_ contains the source code of the tutorial from the
jMonkeyEngine git repository (https://github.com/jMonkeyEngine/jmonkeyengine.git)
on GitHub,
[Commit d128d96 (May 2025)](https://github.com/jMonkeyEngine/jmonkeyengine/commit/d128d96).

The other subprojects belong to _Droids_. The _:droids:model_ subproject
contains the code of the game model, _:droids:app_ the game view and game
controller as well as the main class _pp.droids.DroidsApp_, and _:droids:json_
is used to serialize and deserialize models using the deserialization of models
using JSON. The subprojects _:jme-common_ and _:graphics_ contain auxiliary
classes.

The directory `droids/doc` contains some diagrams (in _PlantUML_- as well as in
_PNG_ format) about _Droids_.

## 1 Preparation

We recommend using Java 23 for the programming project. Under
Linux, [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=23)
should be used as the JDK; other JDKs may cause problems under Linux. However,
we also recommend Temurin on other operating systems.

The following describes how to install Temurin and how to set the environment
variable **JAVA_HOME** correctly so that you can use Gradle (see below).

### 1.1 Installing Temurin

Download [_Eclipse Temurin_](https://adoptium.net/temurin/releases/?version=23)
according to your operating system and processor architecture and
unpack the archive in a directory of your choice on your computer.

### 1.2 Setting JAVA_HOME

For use with Gradle the environment variable **JAVA_HOME** must be set correctly.
To do this, follow the instructions below according to your
operating system:

* **Windows**:

  Open your Powershell (Core) or Windows Terminal with Powershell (Core). Check whether the environment variable is set
  correctly:

  > `Get-ChildItem -Path Env:JAVA_HOME`

  If no path or an incorrect path is set, set it with the following command (in
  one line):

  > `[System.Environment]::SetEnvironmentVariable('JAVA_HOME','<Path to SDK>',[System.EnvironmentVariableTarget]::User)`

  Alternatively, you can use the GUI. In Windows 10, click the Windows key and
  then the gear icon to open the settings. Select "System," then "Info" (bottom
  left) and then "Advanced system settings" (right) to open the "System
  Properties" dialog. In the "Advanced" tab, click "Environment Variables..."
  and then click the "New..." button under "User variables" to create JAVA_HOME
  or "Edit" to change it. Enter `JAVA_HOME` as the name and the path as the
  value. Close with "OK".

  > **(!) Please note that you must restart the respective application** to take
  note of the set environment variable. This also applies to the shell (command
  line) you are currently using.

* **UNIX (Linux/MacOS):**

  Open or create the file `~/.profile` (if you are using Bash; other shells use
  different files) and add the following line at the end of the file:

  > `export JAVA_HOME="<path to the unzipped archive>"`

  Replace `<path to the unzipped archive>` with the appropriate path. For example:

  > `export JAVA_HOME="/home/user/jdk-23.0.2"`

  Then add the following line:

  > `export PATH="$JAVA_HOME/bin:$PATH"`

## 2 Program start

You can basically just open the whole project in IntelliJ. Please make sure that
you are using Java 23 also in IntelliJ, and in particular in the Gradle
settings. The following describes how the single programs (_Droids_ and the
programs of the tutorial) can be started directly from the command line.

The easiest way to execute *Droids* without IntelliJ is by entering the command

> `./gradlew :droids:app:run`

in the **command line**.

Alternatively, you can create executable programs and start scripts by

> `./gradlew installDist`

When finished, *Droids* can be started by executing the generated start script

> `./droids/app/build/install/droids/bin/droids`

in the **command line**. On Windows, depending on the shell (cmd prompt) it may be
necessary to replace `/` with `\ ` in each case.

The individual programs of the tutorial can be started using one of the
start scripts, which can be found in the directory

> `./jme-helloworld/build/install/HelloJME3/bin`

directory, e.g.,

> `./jme-helloworld/build/install/HelloJME3/bin/HelloJME3`

The names of these scripts correspond to the class names in the tutorial.

## 3 Notes on the game _Droids_

1) *Droids* has a menu where you start with the following options:

    * `Create random map`: Changes the scene and creates a new, random scene.
    * `Load map from file...`: Opens a dialog where you can enter the path to a
      JSON file containing a previously saved scene.
    * `Save map in file...`: Opens a dialog where you can enter the path to a
      JSON file where you want to save the current scene.
    * `Return to game`: Closes the menu and resumes (or starts) the game.
    * `Quit game`: Exits the game.

   There are also some buttons that allow you to turn on/off sound effects, the
   radar view, and whether the radar shows the Droid's navigation path after
   you click somewhere in the game (see below).

   Within the game, you can use the following keys to control the game:

    * `Esc`: Pause the game and open the menu.
    * `W` or `Cursor up`: Droid walks forward.
    * `S` or `Cursor down`: Droid walks backward.
    * `A` or `Cursor right`: Droid turns left.
    * `D`or `Cursor right`: Droid turns right.
    * `Space`: Droid shoots.
    * `M`: switches sound effects on or off.
    * `R`: switches the radar map on or off..
    * `X`: switches aggressive behavior of the opposing robots (they shoot at the
      Droid) on or off.

   You can also click anywhere with the mouse. This will cast an imaginary ray
   into the scene. The Droid will automatically start walking to the point where
   the ray hits the ground, if it can reach that point.

   A game ends when the Droid reaches the finish line (the black and white
   patterned "carpet"), or when the opposing robots kill the Droid.

2) When the game is started, the **current working directory** is searched for a
   file `config.properties` where default settings of the game can be changed.
   Such a file can be found in the directory `droids/app` directory.

## 4 General Gradle tasks:

- > `./gradlew clean`

  Removes all `build` directories and all created files.

- > `./gradlew classes`

  Builds the source code and stores bytecode as well as resources in the build
  directory.

- > `./gradlew javadoc`

  Generates the documentation from the JavaDoc comments in the directory
  `build/docs/javadoc` of the respective subproject.

- > `./gradlew test`

  Executes the JUnit tests. Results are stored in the directory
  `build/reports/tests` of the respective subproject.

- > `./gradlew build`

  Runs the JUnit tests and builds distribution files in `build/distributions`.

- > `./gradlew installDist`

  Creates a directory under `droids/app/build/install` which contains an
  executable distribution including startup scripts (see above).

- > `./gradlew installShadowDist`

  Creates start scripts like `./gradlew installDist`, but combines all jar files
  into a single "fat" jar file.

---
May 2025