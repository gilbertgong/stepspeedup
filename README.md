<p align="center"><img src="graphics/banner.png" alt="StepSpeedup"></p>

**StepSpeedup**, a Starsector utility mod that lets you step the game speed up and down with the
`+` and `-` keys, through a list of (configurable) speed multipliers:

```
x1  →  x2  →  x4  →  x6  →  x8  →  x16
```

This works in both combat and the campaign.
It is recommended when using this mod to enable the `Campaign "speed up time" is a toggle`
game setting in Starsector, and keep "speed up" enabled.  You can step down to 1x for normal speed.
However, it does also work with the standard non-toggle setting as well.

## Why another speed mod?

[SpeedUp](https://fractalsoftworks.com/forum/index.php?topic=13394.0) by
DarkRevenant is the established speed mod, and it works well. It is built around
separate hotkeys that each hold or toggle a fixed multiplier, and multipliers
from several active hotkeys stack together.

StepSpeedup takes a simpler approach: one list of speeds, and two keys to move
through it. Rather than enabling and disabling speedup, this mod also allows
you to step down to a 1x speed, making it always simple to speed up or slow down.

## AI Disclaimer

The majority of this mod was written by Claude (including creation of some graphics).
I performed some manual additions, edits, and changes, and have reviewed every line of code in this mod/repo.

## How it works

### Combat

`+` and `-` change the combat engine's time multiplier directly. Every battle
starts at x1 (with an option to preserve multiplier between battles).
A status line is displayed for each change. Speeds below 1x work in combat.

### Campaign

In the campaign, the game's built-in "speed up" function is relied upon and it is
recommended to use the `Campaign "speed up time" is a toggle` game setting with this mod.
Enable "speed up time" (vanilla default is "shift" key), and then use `+` and `-`
to step through speeds, including down to 1x for normal speed.

### Speeds below x1

The game only supports speeds below x1 (slow motion) in combat. You can add
values like `0.5` to the speed list, but they only have an effect in combat.

## Installation

1. Copy the `stepspeedup` folder into your Starsector `mods` folder.
2. Enable **StepSpeedup** in the launcher.

It's a utility mod, so it can be added to or removed from an existing save.

## Configuration

Settings are in `data/config/StepSpeedup.json`:

| Setting | Default | Description |
|---|---|---|
| `speeds` | `[1, 2, 4, 6, 8, 16]` | The speed list. x1 and x2 are always included, and the list is sorted automatically. |
| `speedUpKeys` | `[13, 78]` | Keys that step up: `=`/`+` and numpad `+`. Uses [LWJGL key codes](https://gist.github.com/Mumfrey/5cfc3b7e14fef91b6fa56470dc05218a). |
| `slowDownKeys` | `[12, 74]` | Keys that step down: `-` and numpad `-`. |
| `allowKeyRepeat` | `false` | If true, holding a key keeps stepping. |
| `resetEachBattle` | `true` | Reset combat speed to x1 at the start of each battle. |
| `enableCampaign` | `true` | Control the campaign fast-forward speed. |
| `showMessages` | `true` | Show a message when the speed changes. |

1x and 2x speeds are always included regardless of the configuration file, as this simplifies the code with fewer
corner cases and degenerate configurations to test.

Key presses with Ctrl or Alt held are ignored, so they're left for the game and
other mods.

Very high speeds make combat less accurate, because the game simulates bigger
time steps per frame.
