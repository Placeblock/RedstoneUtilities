## RedstoneUtilities

- [Wireless Redstone](#wireless-redstone)
- [AutoCrafter](#autocrafter)
- [ItemFilter](#itemfilter)
- [ChunkLoader](#chunkloader)
- [Upgrades](#upgrades)

### Wireless Redstone

Wireless Redstone consists of two blocks. The sender and the receiver.
You can place them just as a block or directly on top of Redstone.

When placed you can connect them by right-clicking with the Connector on 
the receiver and on the sender. Do it again and they will be unlinked.

With the Infometer you can gather information about senders and receivers. 
By shift-right-clicking on a receiver you can add the receiver to the GUI of the 
Infometer. If you now open the Infometer's GUI by right-clicking the Infometer you
can control all registered receivers.

To structure your senders and receivers a bit better you can right-click with
any block on senders and receivers to differentiate them. This doesn't have an effect
on the Redstone logic, it's just to help managing them.

<a href="https://ibb.co/87HKPSG">
    <img src="https://i.ibb.co/87HKPSG/2023-07-02-15-45-59.png" alt="2023-07-02-15-45-59">
</a> 
<a href="https://ibb.co/DMV5NMV">
    <img src="https://i.ibb.co/DMV5NMV/2023-07-02-15-46-16.png" alt="2023-07-02-15-46-16">
</a> 
<a href="https://ibb.co/ZX9Bmpj">
    <img src="https://i.ibb.co/ZX9Bmpj/2023-07-02-15-46-48.png" alt="2023-07-02-15-46-48">
</a> 
<a href="https://ibb.co/BfKdYJt">
    <img src="https://i.ibb.co/BfKdYJt/2023-07-02-15-47-01.png" alt="2023-07-02-15-47-01">
</a> 
<a href="https://ibb.co/C6NF9hc">
    <img src="https://i.ibb.co/C6NF9hc/2023-07-02-15-48-19.png" alt="2023-07-02-15-48-19">
</a>

### AutoCrafter

The AutoCrafter can be placed on a dropper and filled by putting items inside the hopper.
The hopper will not be visible anymore, however you can still interact with it and
insert items e.g. with hoppers.
You have to place a hopper below the AutoCrafter, otherwise no items will be crafted.

If you right-click on the crafting grid a gui will open. Here you can change the
crafting recipe and upgrade the AutoCrafter. The following upgrades are possible:
- Speed (Items will get crafted faster: <code>CraftTime (Ticks) = ((6 - level) * 33) + 4)</code> )
- Efficiency (Level 1 - 6 : 1 - 6 Items will be crafted simultaneously)

<a href="https://ibb.co/Z6PdQ9d">
    <img src="https://i.ibb.co/Z6PdQ9d/2023-07-16-16-50-32.png" alt="2023-07-16-16-50-32">
</a>

### ItemFilter

The ItemFilter can be placed just as a block or inside a hopper.
By right-clicking with any item on it, you can set the filter.
The hopper will now accept only the filter item.
However, if you put items directly inside the hopper there is no restriction.
You can remove the filter by right-clicking with nothing in your hand.
If there is now filter set, every item will be accepted!

<a href="https://ibb.co/zXCnL4W">
    <img src="https://i.ibb.co/zXCnL4W/2023-07-29-17-52-07.png" alt="2023-07-29-17-52-07">
</a>

### ChunkLoader

The ChunkLoader can be placed just as a block. It loads a specific amount of chunks.
By right-clicking on the ChunkLoader you can upgrade it. The following upgrades are
possible:
- Efficiency (More chunks will be loaded: <code>Chunk-Square-Radius = Level / 3</code>)

The loaded Chunks have the shape of a square. The different levels 
produce the following squares:
1. 1x1
2. 1x1
3. 3x3
4. 3x3
5. 3x3
6. 5x5

<a href="https://ibb.co/yXgNvkD">
    <img src="https://i.ibb.co/yXgNvkD/2023-07-29-17-53-53.png" alt="2023-07-29-17-53-53">
</a>

### Upgrades

There are two different Upgrades availible:
- Speed (Crafted with a Clock in the center)
- Efficiency (Crafted with a Soul-Campfire in the center)

To complete the crafting recipe you have to put paper around the center and the level-ingredients in
the corners. The specific level-ingredients are the folowing:
1. Coal
2. Iron-Ingot
3. Emerald
4. Gold-Ingot
5. Diamond
6. Netherite-Ingot
