from pyspark.sql import SparkSession
from pyspark.ml.feature import Tokenizer, StopWordsRemover, CountVectorizer, StringIndexer
from pyspark.ml.classification import MultilayerPerceptronClassifier
from pyspark.ml import Pipeline
from pyspark.sql.functions import col
from pyspark.sql.types import StringType

# Create a Spark session
spark = SparkSession.builder.appName("SentimentAnalysis").getOrCreate()

# Load CSV file into a DataFrame
data = spark.read.csv("TrainingData.csv", inferSchema=True, header=False)

# Rename columns for clarity
data = data.withColumnRenamed("_c0", "label").withColumnRenamed("_c1", "text")

# Cast the "label" column to StringType
data = data.withColumn("label", col("label").cast(StringType()))

# Tokenize text column
tokenizer = Tokenizer(inputCol="text", outputCol="words")
data = tokenizer.transform(data)

# Remove stopwords
remover = StopWordsRemover(inputCol="words", outputCol="filtered_words")
data = remover.transform(data)

# Convert words to feature vectors
vectorizer = CountVectorizer(inputCol="filtered_words", outputCol="features")
vectorizer_model = vectorizer.fit(data)
data = vectorizer_model.transform(data)

# Create a StringIndexer to convert string labels to numeric values
label_indexer = StringIndexer(inputCol="label", outputCol="label_numeric")
data = label_indexer.fit(data).transform(data)

# Select columns needed for training
data = data.select("label_numeric", "features")

# Split data into training and testing sets
train_data, test_data = data.randomSplit([0.8, 0.2], seed=123)

# Define the neural network layers
layers = [len(vectorizer_model.vocabulary), 64, 3]

# Create the Multilayer Perceptron Classifier
mlp = MultilayerPerceptronClassifier(layers=layers, seed=123, labelCol="label_numeric", featuresCol="features")

# Create and Fit Pipeline
pipeline = Pipeline(stages=[mlp])
model = pipeline.fit(train_data)

# Save the trained model
model.save("Models")

# Stop the Spark session
spark.stop()
