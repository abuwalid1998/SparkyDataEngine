import pandas as pd

# Load the CSV file into a pandas DataFrame
file_path = "Cleandfile.csv"
df = pd.read_csv(file_path)

# Count the number of zeros in each row
num_zeros = (df.iloc[:, 2:] == 0).sum(axis=1)

# Identify rows with more than 15 zeros or less than 0
rows_to_remove = (num_zeros > 15) | (num_zeros < 15)

# Remove the identified rows
cleaned_df = df[~rows_to_remove]

# Save the cleaned DataFrame back to a new CSV file
cleaned_file_path = "FinalCleanedFile.csv"
cleaned_df.to_csv(cleaned_file_path, index=False)
