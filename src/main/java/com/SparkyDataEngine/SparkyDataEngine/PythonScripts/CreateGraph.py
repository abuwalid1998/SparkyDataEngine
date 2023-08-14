import pandas as pd
import matplotlib.pyplot as plt

# Load the CSV file into a pandas DataFrame
file_path = "Cleandfile.csv"
df = pd.read_csv(file_path)

# Define the node column ranges for each node
node_ranges = {
    1: ['(Node 01) Battery Voltage', '(Node 01) Soil EC', '(Node 01) Soil Moisture', '(Node 01) Soil Temperature',
        '(Node 01) Solar Voltage'],
}

# Calculate average data for each node and each data type
for node, columns in node_ranges.items():
    node_data = df[columns]
    node_averages = node_data.mean()

    # Plotting the graph for the current node
    plt.figure(figsize=(10, 6))
    node_averages.plot(kind='bar')
    plt.title(f'Average Data for Node {node}')
    plt.xlabel('Data Types')
    plt.ylabel('Average Value')
    plt.xticks(rotation=0)
    plt.tight_layout()
    plt.show()