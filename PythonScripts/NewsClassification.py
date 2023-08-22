import pandas as pd
from keras.preprocessing.text import Tokenizer
from keras.preprocessing.sequence import pad_sequences
from keras.layers import Embedding, LSTM, Dense, Dropout
from keras.models import Sequential
import numpy as np

# Load data from CSV
file_path = "TrainingDatafile.csv"
data = pd.read_csv(file_path, encoding='latin-1')
# Replace with your CSV file path

sentences = data['text'].tolist()
labels = data['label'].tolist()

# Tokenize and preprocess
tokenizer = Tokenizer(num_words=10000, oov_token="<OOV>")
tokenizer.fit_on_texts(sentences)
sequences = tokenizer.texts_to_sequences(sentences)
padded_sequences = pad_sequences(sequences, maxlen=100, padding="post", truncating="post")

# Create the model
model = Sequential([
    Embedding(input_dim=10000, output_dim=16, input_length=100),
    LSTM(64, return_sequences=True),
    Dropout(0.5),
    LSTM(64),
    Dense(64, activation="relu"),
    Dropout(0.5),
    Dense(1, activation="sigmoid")
])

# Compile the model
model.compile(loss="binary_crossentropy", optimizer="adam", metrics=["accuracy"])

# Train the model
model.fit(np.array(padded_sequences), np.array(labels), epochs=10)

# Save the model
model.save("sentiment_analysis_model2")
