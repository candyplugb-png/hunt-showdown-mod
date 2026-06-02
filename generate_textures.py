#!/usr/bin/env python3
from PIL import Image, ImageDraw
import os

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                    "src/main/resources/assets/huntmod/textures")

def ensure_dir(path):
    os.makedirs(path, exist_ok=True)

def make_dark_nest_block():
    img = Image.new("RGBA", (16, 16), (10, 5, 15, 255))
    draw = ImageDraw.Draw(img)
    for x, y in [(2,3),(3,2),(7,1),(8,8),(5,12),(11,6),(14,9),(1,14)]:
        draw.point((x, y), fill=(80, 0, 0, 255))
        draw.point((x+1, y), fill=(60, 0, 0, 200))
    for i in range(16):
        draw.point((i, 0),  fill=(120, 60, 0, 80))
        draw.point((i, 15), fill=(120, 60, 0, 80))
        draw.point((0,  i), fill=(120, 60, 0, 80))
        draw.point((15, i), fill=(120, 60, 0, 80))
    path = os.path.join(BASE, "block", "dark_nest.png")
    ensure_dir(os.path.dirname(path))
    img.save(path)
    print(f"Created: {path}")

def make_dark_nest_egg():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    draw.ellipse([3, 1, 12, 14], fill=(15, 8, 20, 255), outline=(80, 0, 0, 255))
    draw.line([(7, 4), (9, 7)],  fill=(180, 0, 0, 255), width=1)
    draw.line([(6, 8), (8, 11)], fill=(140, 0, 0, 255), width=1)
    draw.point((8, 6), fill=(255, 80, 0, 200))
    path = os.path.join(BASE, "item", "dark_nest_egg.png")
    ensure_dir(os.path.dirname(path))
    img.save(path)
    print(f"Created: {path}")

def make_dark_sight_effect():
    img = Image.new("RGBA", (18, 18), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    draw.ellipse([2, 5, 15, 13], fill=(30, 0, 0, 200), outline=(200, 0, 0, 255))
    draw.ellipse([7, 7, 11, 11], fill=(180, 0, 0, 255))
    draw.point((8, 8), fill=(255, 100, 100, 255))
    path = os.path.join(BASE, "mob_effect", "dark_sight.png")
    ensure_dir(os.path.dirname(path))
    img.save(path)
    print(f"Created: {path}")

if __name__ == "__main__":
    print("Generating textures...")
    make_dark_nest_block()
    make_dark_nest_egg()
    make_dark_sight_effect()
    print("Done!")
