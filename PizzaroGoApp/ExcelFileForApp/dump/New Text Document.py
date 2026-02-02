# Regenerating Excel menu with 48 pizzas (4 sizes x 12 types), 6 sauces, and 18 drinks with full structure and image URLs

import pandas as pd
import os
import sys

# Add script directory to path
script_dir = r"c:\Users\stoic\Desktop\Academy\Project\PizzaroGo\PizzaroGoApp\ExcelFileForApp"
sys.path.insert(0, script_dir)
os.chdir(script_dir)

# Define pizza types and base data
pizza_data = [
    ("Margherita", 78, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Busuioc 0.02 Kg", "https://turn0image1468"),
    ("Diavola", 83, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Salam 0.24 Kg, Oregano 0.02 Kg", "https://turn0image1470"),
    ("Quattro Formaggi", 86, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Oregano 0.02 Kg", "https://turn0image1498"),
    ("Capricciosa", 78, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Sunca 0.24 Kg, Ciuperci 0.16 Kg, Masline 0.08 Kg", "https://turn0image1529"),
    ("Quattro Stagioni", 86, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Sunca 0.24 Kg, Ciuperci 0.16 Kg, Masline 0.08 Kg, Ardei 0.13 Kg", "https://turn0image1557"),
    ("Tonno", 83, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Ton 0.19 Kg, Ceapa 0.1 Kg", "https://turn0image1590"),
    ("Hawaii", 83, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Sunca 0.24 Kg, Ananas 0.16 Kg", "https://turn0image1619"),
    ("Vegetariana", 78, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Ciuperci 0.16 Kg, Ardei 0.13 Kg, Ceapa 0.1 Kg, Masline 0.08 Kg, Porumb 0.08 Kg", "https://turn0image1647"),
    ("Pepperoni", 83, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Salam 0.24 Kg", "https://turn0image1677"),
    ("BBQ Chicken", 86, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Ceapa 0.1 Kg, Oregano 0.02 Kg", "https://turn0image1707"),
    ("Carbonara", 86, "Faina 0.4 Kg, Mozzarella 0.32 Kg, Sunca 0.24 Kg, Oregano 0.02 Kg", "https://turn0image1737"),
    ("Rustica", 83, "Faina 0.4 Kg, Sos de rosii 0.24 L, Mozzarella 0.32 Kg, Rosii 0.16 Kg, Ceapa 0.1 Kg, Masline 0.08 Kg", "https://turn0image1767"),
]

sizes = [("Mică", 0.7), ("Medie", 0.9), ("Mare", 1.2), ("XL", 1.6)]

def scale_ingredients(description, factor):
    import re
    def scale(match):
        value, unit = float(match.group(1)), match.group(2)
        return f"{round(value * factor, 2)} {unit}"
    return re.sub(r"([\d.]+)\s*(Kg|L)", scale, description)

menu = []

# Generate pizzas
for name, base_price, base_desc, img_url in pizza_data:
    for size_name, factor in sizes:
        full_name = f"{name} {size_name}"
        price = round(base_price * factor)
        desc = scale_ingredients(base_desc, factor)
        menu.append({
            "Name": full_name,
            "Category": "PIZZA",
            "Price": price,
            "Description": desc,
            "Image URL": img_url
        })

# Add sauces
sauces = ["usturoi", "ketchup", "maioneza", "BBQ", "picant", "de rosii servire"]
sauce_img = "https://cdn-icons-png.flaticon.com/512/3174/3174880.png"
for s in sauces:
    menu.append({
        "Name": f"Sos {s}",
        "Category": "SAUCE",
        "Price": 5,
        "Description": f"Sos {s} 1 piece",
        "Image URL": sauce_img
    })

# Add drinks
brands = ["Pepsi", "Coca-Cola", "Fanta", "Sprite", "Apa plata", "Ice Tea"]
volumes = ["0.5L", "1L", "2L"]
drink_img = "https://cdn-icons-png.flaticon.com/512/1046/1046784.png"
for brand in brands:
    for vol in volumes:
        menu.append({
            "Name": f"{brand} {vol}",
            "Category": "DRINK",
            "Price": 9,
            "Description": f"{brand} {vol} 1 piece",
            "Image URL": drink_img
        })

# Create DataFrame and save to Excel
df = pd.DataFrame(menu)
output_path = script_dir + "/final_menu_regenerated.xlsx"
df.to_excel(output_path, index=False)

print("Regenerated Excel menu with 48 pizzas, 6 sauces, and 18 drinks saved as final_menu_regenerated.xlsx")
