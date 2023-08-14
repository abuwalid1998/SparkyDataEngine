from pyspark.sql import SparkSession
from pyspark.ml import PipelineModel
from pyspark.ml.evaluation import MulticlassClassificationEvaluator


spark = SparkSession.builder.appName("ModelEvaluation").getOrCreate()

loaded_model = PipelineModel.load("Models")  # Replace with the actual model path
