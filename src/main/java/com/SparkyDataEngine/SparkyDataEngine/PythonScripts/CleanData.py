import pandas as pd

# Load the CSV file into a pandas DataFrame
file_path = "January_2023.csv"
df = pd.read_csv(file_path)

node_ranges = {
    1: ['(Node 01) Battery Voltage', '(Node 01) Soil EC', '(Node 01) Soil Moisture', '(Node 01) Soil Temperature', '(Node 01) Solar Voltage'],
    2: ['(Node 02) Battery Voltage', '(Node 02) Soil EC', '(Node 02) Soil Moisture', '(Node 02) Soil Temperature', '(Node 02) Solar Voltage'],
    3: ['(Node 03) Battery Voltage', '(Node 03) Soil EC', '(Node 03) Soil Moisture', '(Node 03) Soil Temperature', '(Node 03) Solar Voltage'],
    4: ['(Node 04) Battery Voltage', '(Node 04) Soil EC', '(Node 04) Soil Moisture', '(Node 04) Soil Temperature', '(Node 04) Solar Voltage']
}

# Replace null values with 0 for each node's columns
for node, columns in node_ranges.items():
    df[columns] = df[columns].fillna(0)


cleaned_file_path = "Cleandfile.csv"
df.to_csv(cleaned_file_path, index=False)
