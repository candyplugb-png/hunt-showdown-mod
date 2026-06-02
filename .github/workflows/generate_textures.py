#!/usr/bin/env python3
"""
Generates placeholder textures for the Hunt Showdown mod.
Run this ONCE after cloning to create basic textures so the mod compiles.
You can replace them later with proper artwork.

Requirements: Pillow
  pip install Pillow
"""

from PIL import Image, ImageDraw
import os

BASE = os.path.join(os.path.dirname(__file__),
                    "src/main/resources/assets/huntmod/textures")

def ensure_dir(path):
    os.makedirs(path, exist_ok=True)

# ─── 1. dark_nest block texture (16×16) ──────────────────────────────────────
def make_dark_nest_block():
    img = Image.new("RGBA", (16, 16), (10, 5, 15, 255))  # near-black purple
    draw = ImageDraw.Draw(img)
    # Dark red veins
    for x, y in [(2,3),(3,2),(7,1),(8,8),(5,12),(11,6),(14,9),(1,14)]:
        draw.point((x, y), fill=(80, 0, 0, 255))
        draw.point((x+1, y), fill=(60, 0, 0, 200))
    # Rim glow (amber)
    for i in range(16):
        draw.point((i, 0),  fill=(120, 60, 0, 80))
        draw.point((i, 15), fill=(120, 60, 0, 80))
        draw.point((0,  i), fill=(120, 60, 0, 80))
        draw.point((15, i), fill=(120, 60, 0, 80))
    path = os.path.join(BASE, "block", "dark_nest.png")
    ensure_dir(os.path.dirname(path))
    img.save(path)
    print(f"✓ {path}")

# ─── 2. dark_nest_egg item texture (16×16) ───────────────────────────────────
def make_dark_nest_egg():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # Egg shape (ellipse)
    draw.ellipse([3, 1, 12, 14], fill=(15, 8, 20, 255), outline=(80, 0, 0, 255))
    # Cracks (red lines)
    draw.line([(7, 4), (9, 7)],  fill=(180, 0, 0, 255), width=1)
    draw.line([(6, 8), (8, 11)], fill=(140, 0, 0, 255), width=1)
    # Glow dot
    draw.point((8, 6), fill=(255, 80, 0, 200))
    path = os.path.join(BASE, "item", "dark_nest_egg.png")
    ensure_dir(os.path.dirname(path))
    img.save(path)
    print(f"✓ {path}")

# ─── 3. dark_sight mob effect icon (18×18) ───────────────────────────────────
def make_dark_sight_effect():
    img = Image.new("RGBA", (18, 18), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # Eye whites (dark red)
    draw.ellipse([2, 5, 15, 13], fill=(30, 0, 0, 200), outline=(200, 0, 0, 255))
    # Pupil
    draw.ellipse([7, 7, 11, 11], fill=(180, 0, 0, 255))
    # Glint
    draw.point((8, 8), fill=(255, 100, 100, 255))
    path = os.path.join(BASE, "mob_effect", "dark_sight.png")
    ensure_dir(os.path.dirname(path))
    img.save(path)
    print(f"✓ {path}")

if __name__ == "__main__":
    print("Generating placeholder textures...")
    make_dark_nest_block()
    make_dark_nest_egg()
    make_dark_sight_effect()
    print("\nDone! You can replace these with proper art later.")
