import pandas as pd

# Load the CSV file into a pandas DataFrame
file_path = "January_2023.csv"
df = pd.read_csv(file_path)

# Print the column names to verify their format
print(df.columns)