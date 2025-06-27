# jMonkeyEngine Beginner Tutorials

In diesem Projekt sind die [_jMonkeyEngine Beginner
Tutorials_](https://wiki.jmonkeyengine.org/docs/3.4/tutorials/beginner/beginner.html)
zusammengefasst und um Gradle-Unterstützung erweitert, damit Sie die Tutorials
unmittelbar ausführen können, insbesondere in IntelliJ.

> Der Quellcode stammt vom
> [jMonkeyEngine-Git-Repository](https://github.com/jMonkeyEngine/jmonkeyengine.git)
> auf GitHub, [Commit d128d96 (Mai
> 2025)](https://github.com/jMonkeyEngine/jmonkeyengine/commit/d128d96). Das
> Beginner Tutorial bezieht sich noch auf eine ältere Version von jMonkeyEngine.
> Der Tutorial-Code im Git-Repository und damit auch in diesem Projekt ist
> dagegen die obige neuere Version. Daher gibt es an einigen Stellen
> geringfügige Unterschiede zum Code auf der Tutorial-Web-Seite.

Das Projekt nutzt Gradle als Build-Tool, das Sie auch im Programmierprojekt
verwenden werden. IntelliJ kann unmittelbar mit Gradle-Projekten umgehen.

## Inhalt

1. Java
2. Gradle
3. Nutzung in IntelliJ IDEA
4. Tutorial
5. Fragen

## 1 Java

Für das Programmierprojekt und das Tutorial empfehlen wir die Verwendung von
Java 23. Unter Linux sollte _Eclipse Temurin_ als JDK verwendet werden, andere
JDKs können unter Linux Probleme verursachen. Auf anderen Betriebssystemen
empfehlen wir aber ebenfalls Temurin. Im Folgenden ist beschrieben, wie Sie
Temurin installieren und die Umgebungsvariablen **JAVA_HOME** und **PATH**
richtig setzen, damit Sie Gradle (siehe unten) verwenden können.

### 1.1 Installation von Temurin

Laden Sie [_Eclipse Temurin_](https://adoptium.net/temurin/releases/?version=23)
entsprechend Ihrem Betriebssystem und Ihrer Prozessorarchitektur herunter und
entpacken Sie das Archiv in einem Verzeichnis Ihrer Wahl auf Ihrem Rechner.

* **Windows:**

  Verwenden Sie unter Windows die msi-Download-Möglichkeit und installieren Sie
  es durch Doppelklick. Achten Sie darauf, dass "Zum PATH hinzufügen" und
  "JAVA_HOME-Variable konfigurieren" gesetzt sind, dass dort also kein rotes X
  ist!

* **UNIX (Linux/MacOS):**

  Entpacken Sie das heruntergeladene Archiv in einem Verzeichnis Ihrer Wahl auf
  Ihrem Rechner.

### 1.2 Kontrollieren und Setzen von JAVA_HOME und PATH

Zur Verwendung mit Gradle muss die Umgebungsvariable **JAVA_HOME** richtig
gesetzt werden. Folgen Sie dazu den nachfolgenden Anweisungen entsprechend Ihrem
Betriebssystem:

* **Windows:**

  Da Sie das msi-Programm mit den richtigen Einstellungen ausgeführt haben, sind
  JAVA_HOME und PATH nun korrekt gesetzt. Sie können dies überprüfen: Öffnen Sie
  ihre Powershell (Core) bzw. ihr Windows Terminal mit Powershell (Core).
  Überprüfen Sie, ob die Umgebungsvariable korrekt gesetzt ist:  
  `Get-ChildItem -Path Env:JAVA_HOME`  
  Der Pfad muss auf das Installationsverzeichnis zeigen, darunter finden Sie
  dann die Verzeichnisse des SDKs (bin, conf, include, ...).  
  `Get-ChildItem -Path Env:PATH`  
  In der Liste muss das bin-Verzeichnis Ihrer Java-Installation erscheinen.
  Darunter befinden sich u.a. java.exe und javac.exe.


  Falls kein oder ein falscher Pfad gesetzt ist, kann es sein, dass die
  Benutzervariablen und Systemvariablen kollidieren oder Sie die Installation
  nicht korrekt durchgeführt haben. Sie können versuchen die Einstellungen
  manuell anzupassen. Unter Windows 10 klicken Sie die Windows-Taste und dann
  das _Zahnrad_ um die Einstellungen zu öffnen. Dort wählen Sie _System_, dann
  _Info_ (links unten) und nun _Erweiterte Systemeinstellungen_ (rechts) um den
  Dialog _Systemeigenschaften_ zu starten. Im Reiter _Erweitert_ klicken Sie
  _Umgebungsvariablen..._ und klicken dann unter _Systemvariablen_ den Knopf
  _Neu..._ um JAVA_HOME anzulegen oder _Bearbeiten_ um sie zu ändern. Geben Sie
  als Name `JAVA_HOME` und als Wert den Pfad ein. Schließen Sie mit _OK_. Da
  `PATH` bereits angelegt sein sollte, können Sie Ihr Java-bin-Verzeichnis mit
  _Bearbeiten_, dann _Neu_ direkt vorne (_Nach oben_) hinzufügen. Beachten Sie,
  dass die Werte nur bei Systemvariablen eingetragen sind, nicht bei beiden
  (Benutzervariablen und Systemvariablen).

  > **(!) Beachten Sie, dass Sie Applikationen neu starten müssen**, um von der
  > gesetzten Umgebungsvariablen Notiz zu nehmen. Dies betrifft auch die Shell,
  > die Sie gerade verwenden!

* **UNIX (Linux/MacOS):**

  Öffnen oder erstellen Sie die Datei `~/.profile`, wenn Sie die bash verwenden,
  bzw. `~/.zshrc`, wenn Sie die zsh verwenden (bei anderen Shells kann es auch 
  eine andere Datei sein), und ergänzen Sie am Ende der Datei die Zeile:

  `export JAVA_HOME="<Pfad zum entpackten Archiv>"`

  Ersetzen Sie dabei `<Pfad zum entpackten Archiv>` mit dem entsprechenden Pfad.
  Zum Beispiel:

  `export JAVA_HOME="/home/user/jdk-23.0.2"`

  Fügen Sie dann die folgende Zeile hinzu:

  `export PATH="$JAVA_HOME/bin:$PATH"`

### 1.3 Java-Version kontrollieren

Rufen Sie in Ihrer Shell nun `java --version` auf. Folgendes sollte in etwa
ausgegeben werden:
```
openjdk version "23.0.2" 2025-01-21
OpenJDK Runtime Environment Temurin-23.0.2+7 (build 23.0.2+7)
OpenJDK 64-Bit Server VM Temurin-23.0.2+7 (build 23.0.2+7, mixed mode, sharing)
```
Der Aufruf `javac --version` gibt dann in etwa folgendes aus:
```
javac 23.0.2
```

## 2 Gradle

Gradle ist ein weit verbreitetes und universelles Build-Tool, das mit
entsprechender Konfiguration das Nachladen von Abhängigkeiten und das
Übersetzen, Ausführen sowie Installieren des Projektes übernehmen kann. Gradle
wird schon seit einigen Jahren im Rahmen des Programmierprojektes genutzt und
vereinfacht die Installation von Bibliotheken und viele weitere Aufgaben, die im
Programmierprojekt anfallen.

Man kann ein Gradle-Projekt wie dieses einfach in IntelliJ öffnen, siehe unten.
Alternativ kann man es aber auch direkt von der Kommandozeile (oder über
entsprechende Kommandos in IntelliJ) übersetzen, ausführen etc. Das ist im
Folgenden beschrieben.

Am einfachsten erzeugt man ausführbare Programme und Start-Skripte mit dem
Befehl

`./gradlew`

Anschließend lassen sich die einzelnen Programme des Tutorials starten, indem
man entsprechend eines der Start-Skripte aufruft, die im Verzeichnis
`build/install/HelloJME3/bin` zu finden sind, also indem man z.B.

`build/install/HelloJME3/bin/HelloJME3`

in der Kommandozeile aufruft. Unter Windows kann es je nach Shell
(Eingabeaufforderung cmd, nicht aber Powershell Core) erforderlich sein, `/`
jeweils durch `\ ` zu ersetzen.

Die Namen dieser Skripte entsprechen den Klassennamen im Tutorial.

HelloJME3 kann man alternativ auch unmittelbar mit Gradle starten, indem man den
Befehl

`./gradlew run`

in der Kommandozeile aufruft.

### Allgemeine Gradle-Tasks

- `./gradlew clean`

  Entfernt alle `build`-Verzeichnisse und alle erzeugten Dateien.

- `./gradlew classes`

  Übersetzt den Quellcode und legt unter build den Bytecode sowie Ressourcen ab.

- `./gradlew build`

  Führt die JUnit-Tests durch und erstellt in `build/distributions` gepackte
  Distributionsdateien

- `./gradlew installDist`

  Erstellt unter `droids/app/build/install` ein Verzeichnis, das eine
  ausführbare Distribution samt Start-Skripten enthält (siehe oben).

## 3 Nutzung in IntelliJ IDEA

Im Folgenden ist beschrieben, wie man das Tutorial in IntelliJ einrichten und
ausführen kann.

### 3.1 Öffnen des Projekts

Starten Sie IntelliJ IDEA und wählen Sie nach einem Klick auf _Open_ (oder _File
→ Open..._, falls bereits ein Projekt geöffnet ist) das Verzeichnis dieses
Projektes (also jme-helloworld, in dem auch diese README.md-Datei liegt) aus.

IntelliJ sollte jetzt damit beginnen, das Projekt zu analysieren. Es wird
feststellen, dass es sich dabei um ein Gradle-Projekt handelt und Gradle mit der
weiteren Verwaltung betrauen. Gradle beginnt dann entsprechend der Konfiguration
alle Abhängigkeiten herunterzuladen und anschließend das Projekt zu bauen.

### 3.2 JDK auswählen

Sollten dabei Fehler oder Warnungen ausgegeben werden, beziehen diese sich
meistens auf das ausgewählte JDK. In solch einem Fall gehen Sie nach _File →
Project Structure_ und wählen dort das eben installierte _Temurin JDK23_ aus.

Gehen Sie anschließend in die Gradle Settings (ganz rechts am Fensterrand den
Reiter _Gradle_ anklicken und dort oben über den _Schraubenschlüssel_ auf
_Gradle Settings..._) und wählen Sie dort als _Gradle JVM_ das _Project SDK_
aus. Anschließend ändern Sie noch die Einstellungen _Build and run using_ sowie
_Run tests using_ auf _IntelliJ IDEA_. Im Anschluss schließen Sie das Fenster
mit einem Klick auf _OK_. Lassen Sie Gradle aktualisieren, indem Sie im
Gradle-View auf die zwei halbkreisförmigen Pfeile klicken (ca. 9 Icons links
neben dem Schraubenschlüssel; es ähnelt dem Recycling-Symbol).

### 3.3 Erstes Tutorial-Beispiel ausführen

Navigieren Sie nun links im Baum nach
`src/main/java/jme3test/helloworld/HelloJME3` und versuchen Sie, die Klasse
auszuführen. Im Erfolgsfall sollte ein Fenster zur Einstellung von
Display-Eigenschaften erscheinen. Wählen Sie dort die kleinste Auflösung und
klicken Sie auf "Continue".

Falls alles klappt, sollte im Anschluss ein Fenster mit einem blauen Würfel in
der Mitte und ein bisschen Text in der linken unteren Ecke erscheinen.

> **Hinweis für Mac-Nutzer**
>
> Auf **Mac-Rechnern** wird der erste Aufruf jedes JME-Programms auf diese Weise
**fehlschlagen** und eine Fehlermeldung erscheinen. Der Fehlermeldungstext
> enthält den Hinweis
>
> `Please run the JVM with -XstartOnFirstThread.`
>
> Öffnen Sie dazu über das Menü Run → Edit Configurations... die soeben
> erstellte Run Configuration von HelloJME3, wählen unter "Modify options" den
> Punkt "Add VM options" und tragen in dem hinzugefügten Feld "VM options" den
> Text `-XstartOnFirstThread` (genau so) ein. Wählen Sie anschließend "Apply"
> und dann "OK". Anschließend sollte sich HelloJME3 ohne Fehlermeldung starten
> lassen.
>
> Dieselbe Erweiterung der Run Configuration müssen Sie dann bei jedem weiteren
> JME-Programm einmal vornehmen. Das sollte ausschließlich auf Mac-Rechnern,
> nicht aber unter Linux oder Windows nötig sen.

## 4 Tutorial

Nun ist alles so weit eingerichtet, dass Sie der Tutorial-Serie unter
[_jMonkeyEngine Beginner
Tutorials_](https://wiki.jmonkeyengine.org/docs/3.4/tutorials/beginner/beginner.html)
folgen können.

Beachten Sie bitte, dass die Tutorials jetzt unmittelbar ausführbar sein
sollten. Die Tutorials beschreiben das Vorgehen mit dem jme3-SDK und nicht mit
einem Gradle-Projekt. Anders als im Tutorial beschrieben, sollten Sie daher
keine Dateien kopieren müssen.

## 5 Fragen

Dokumentation zu JME finden Sie in deren
[_Wiki_](https://wiki.jmonkeyengine.org) und
[_JavaDoc_](https://javadoc.jmonkeyengine.org/v3.6.1-stable/).

Bei Fragen oder Problemen wenden Sie sich an [_Dr.
Weinert_](peter.weinert@unibw.de).

---
Stand: Mai 2025
