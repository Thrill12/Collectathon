# Collectathon

Collectathon is a simple mod aiming to bring collecting back in business!
It allows modpack developers or server owners to create their own trading card-like system of drops.

Each card is a piece of paper, which can be formatted with colours and lore tags. These have a configurable chance to drop from fishing and chest loot.

You can edit the mod's config to your heart's content:
- load the mod once
- edit the `cards.json` inside `config/collectathon`

Example JSON file:
```JSON
[
  {
    "id": "card_steve",         // Unique identifier for your card. Make sure it's unique!
    "displayName": "§6Steve",   // Displayed as the item name
    "lore": [
      "§7Season 1 MVP"          // Each lore entry is a separate line
    ],
    "dropChance": 0.1           // Decimal percentage drop chance to drop each time a fishing and chest loot is triggered
  }
]
```