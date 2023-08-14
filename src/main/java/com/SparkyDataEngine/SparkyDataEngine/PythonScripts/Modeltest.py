from pyspark.sql import SparkSession
from pyspark.ml import PipelineModel
from pyspark.ml.feature import Tokenizer, StopWordsRemover, CountVectorizer
from pyspark.sql.functions import col, concat, lit

from CreateModel import vectorizer_model

# Create a Spark session
spark = SparkSession.builder.appName("ModelTesting").getOrCreate()

# Load the saved model
loaded_model = PipelineModel.load("Models")  # Replace with the actual model path

# Read test data from a file
test_data = spark.read.option("header", "true").csv("bbc_news.csv")  # Replace with your data file path

# Tokenize title and description columns
tokenizer = Tokenizer(inputCol="title", outputCol="title_words")
tokenizer_desc = Tokenizer(inputCol="description", outputCol="description_words")

test_data = tokenizer.transform(test_data)
test_data = tokenizer_desc.transform(test_data)

# Combine title and description words
test_data = test_data.withColumn("combined_words", concat(col("title_words"), lit(" "), col("description_words")))

# Remove stopwords
remover = StopWordsRemover(inputCol="combined_words", outputCol="filtered_words")
test_data = remover.transform(test_data)

# Convert words to feature vectors using the same vectorizer model
test_data = loaded_model.transform(test_data)

# Select columns needed for prediction
test_data = test_data.select("features")

# Make predictions
test_predictions = loaded_model.transform(test_data)
test_predictions.select("title", "description", "prediction").show(truncate=False)

# Stop Spark session
spark.stop()
