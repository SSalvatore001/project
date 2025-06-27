// Styling of Lemur components
// For documentation, see
// https://github.com/jMonkeyEngine-Contributions/Lemur/wiki/Styling

import com.simsilica.lemur.Button
import com.simsilica.lemur.Button.ButtonAction
import com.simsilica.lemur.Command
import com.simsilica.lemur.HAlignment
import com.simsilica.lemur.Insets3f
import com.simsilica.lemur.component.IconComponent
import com.simsilica.lemur.component.QuadBackgroundComponent
import com.simsilica.lemur.component.TbtQuadBackgroundComponent

def bgColor = color(0.25, 0.5, 0.5, 1)
def buttonEnabledColor = color(0.8, 0.9, 1, 1)
def buttonDisabledColor = color(0.8, 0.9, 1, 0.2)
def buttonBgColor = color(0, 0.75, 0.75, 1)
def sliderColor = color(0.6, 0.8, 0.8, 1)
def sliderBgColor = color(0.5, 0.75, 0.75, 1)
def gradientColor = color(0.5, 0.75, 0.85, 0.5)
def tabbuttonEnabledColor = color(0.4, 0.45, 0.5, 1)

def gradient = TbtQuadBackgroundComponent.create(
        texture(name: "/com/simsilica/lemur/icons/bordered-gradient.png",
                generateMips: false),
        1, 1, 1, 126, 126,
        1f, false)

def doubleGradient = new QuadBackgroundComponent(gradientColor)
doubleGradient.texture = texture(name: "/com/simsilica/lemur/icons/double-gradient-128.png",
                                 generateMips: false)

selector("pp") {
    font = font("Interface/Fonts/Metropolis/Metropolis-Regular-32.fnt")
}

selector("label", "pp") {
    insets = new Insets3f(2, 2, 2, 2)
    color = buttonEnabledColor
}

selector("header", "pp") {
    font = font("Interface/Fonts/Metropolis/Metropolis-Bold-42.fnt")
    insets = new Insets3f(2, 2, 2, 2)
    color = color(1, 0.5, 0, 1)
    textHAlignment = HAlignment.Center
}

selector("container", "pp") {
    background = gradient.clone()
    background.setColor(bgColor)
}

selector("slider", "pp") {
    background = gradient.clone()
    background.setColor(bgColor)
}

def pressedCommand = new Command<Button>() {
    void execute(Button source) {
        if (source.isPressed())
            source.move(1, -1, 0)
        else
            source.move(-1, 1, 0)
    }
}

def enabledCommand = new Command<Button>() {
    void execute(Button source) {
        if (source.isEnabled())
            source.setColor(buttonEnabledColor)
        else
            source.setColor(buttonDisabledColor)
    }
}

def repeatCommand = new Command<Button>() {
    private long startTime
    private long lastClick

    void execute(Button source) {
        // Only do the repeating click while the mouse is
        // over the button (and pressed of course)
        if (source.isPressed() && source.isHighlightOn()) {
            long elapsedTime = System.currentTimeMillis() - startTime
            // After half a second pause, click 8 times a second
            if (elapsedTime > 500 && elapsedTime > lastClick + 125) {
                source.click()

                // Try to quantize the last click time to prevent drift
                lastClick = ((elapsedTime - 500) / 125) * 125 + 500
            }
        }
        else {
            startTime = System.currentTimeMillis()
            lastClick = 0
        }
    }
}

def stdButtonCommands = [
        (ButtonAction.Down)    : [pressedCommand],
        (ButtonAction.Up)      : [pressedCommand],
        (ButtonAction.Enabled) : [enabledCommand],
        (ButtonAction.Disabled): [enabledCommand]
]

def sliderButtonCommands = [
        (ButtonAction.Hover): [repeatCommand]
]

selector("title", "pp") {
    color = color(0.8, 0.9, 1, 0.85f)
    highlightColor = color(1, 0.8, 1, 0.85f)
    shadowColor = color(0, 0, 0, 0.75f)
    shadowOffset = vec3(2, -2, -1)
    background = new QuadBackgroundComponent(color(0.5, 0.75, 0.85, 1))
    background.texture = texture(name: "/com/simsilica/lemur/icons/double-gradient-128.png",
                                 generateMips: false)
    insets = new Insets3f(2, 2, 2, 2)

    buttonCommands = stdButtonCommands
}


selector("button", "pp") {
    background = gradient.clone()
    color = buttonEnabledColor
    background.setColor(buttonBgColor)
    insets = new Insets3f(2, 2, 2, 2)

    buttonCommands = stdButtonCommands
}

selector("slider", "pp") {
    insets = new Insets3f(1, 3, 1, 2)
}

selector("slider", "button", "pp") {
    background = doubleGradient.clone()
    //background.setColor(sliderBgColor)
    insets = new Insets3f(0, 0, 0, 0)
}

selector("slider.thumb.button", "pp") {
    text = "[]"
    color = sliderColor
}

selector("slider.left.button", "pp") {
    text = "-"
    background = doubleGradient.clone()
    //background.setColor(sliderBgColor)
    background.setMargin(5, 0)
    color = sliderColor

    buttonCommands = sliderButtonCommands
}

selector("slider.right.button", "pp") {
    text = "+"
    background = doubleGradient.clone()
    //background.setColor(sliderBgColor)
    background.setMargin(4, 0)
    color = sliderColor

    buttonCommands = sliderButtonCommands
}

selector("slider.up.button", "pp") {
    buttonCommands = sliderButtonCommands
}

selector("slider.down.button", "pp") {
    buttonCommands = sliderButtonCommands
}

selector("checkbox", "pp") {
    onView = new IconComponent("/Interface/Icons/on-button.png", 1f, 5, 0, 1f, false);
    offView = new IconComponent("/Interface/Icons/off-button.png", 1f, 5, 0, 1f, false);
}

selector("rollup", "pp") {
    background = gradient.clone()
    background.setColor(bgColor)
}

selector("tabbedPanel", "pp") {
    activationColor = buttonEnabledColor
}

selector("tabbedPanel.container", "pp") {
    background = null
}

selector("tab.button", "pp") {
    background = gradient.clone()
    background.setColor(bgColor)
    color = tabbuttonEnabledColor
    insets = new Insets3f(4, 2, 0, 2)

    buttonCommands = stdButtonCommands
}
