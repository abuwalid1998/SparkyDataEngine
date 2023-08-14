package com.SparkyDataEngine.SparkyDataEngine.DataServices;


import com.SparkyDataEngine.SparkyDataEngine.DataServices.Tools.WordsVector;
import com.SparkyDataEngine.SparkyDataEngine.Models.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaSparkContext;

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.feature.CountVectorizerModel;
import org.apache.spark.ml.feature.StopWordsRemover;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.*;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.IOException;
import java.util.*;

@Service
public class DataEngine {

    //this Class Should Arrange the Data and gives the right format for data to analyze and Clean it , handle missing values
    //the data is in .CSV file , Sql database , External API'S
    //Example to import .CSV file and Arrange the data form Large to small businesses

    final
    WordsVector wordsVector;

    public DataEngine(WordsVector wordsVector) {
        this.wordsVector = wordsVector;
    }

    public OutMessage ArrangeData(InputData data) {


        try {

            System.out.println(">>>>>>>>>>>>>>>>>" + data.getSparkConfig());

            SparkSession spark = SparkSession.builder()
                    .appName(data.getAppName())
                    .master(data.getSparkConfig())
                    .getOrCreate();

            // Load the financial data from a CSV file into a DataFrame
            Dataset<Row> financialData = spark.read()
                    .option("header", data.isReadHeaders())
                    .csv(data.getFilePath());


            if (data.getFunction() == 1) {
                Dataset<Row> sortedData = financialData.select(data.getName(), data.getValueCol())
                        .orderBy(functions.col(data.getValueCol()).desc());
                sortedData.show();

                String outputCsvPath = "Results//ARRANGED_MYCSVFILENAME.csv";

                sortedData.coalesce(1).write()
                        .option("header", true)
                        .csv(outputCsvPath);


            } else {
                Dataset<Row> sortedData = financialData.select(data.getName(), data.getValueCol())
                        .orderBy(functions.col(data.getValueCol()).asc());
                sortedData.show();

            }

            // Stop the SparkSession
            spark.stop();

            return new OutMessage("Data Arranged Succssfully", "No Errors Found", true);

        } catch (Exception e) {

            return new OutMessage("Data Not Arranged Error Found", e.getMessage(), false);

        }
    }

    public OutMessage CleanData(InputData indata) {
        try {


            SparkSession spark = SparkSession.builder()
                    .appName(indata.getAppName())
                    .master(indata.getSparkConfig())
                    .getOrCreate();


            Dataset<Row> data = spark.read()
                    .option("header", true)
                    .csv(indata.getFilePath());


            List<String> columnsToHandleList = new ArrayList<>();
            columnsToHandleList.add(indata.getValueCol());
            columnsToHandleList.add(indata.getName());


            String[] columnsToHandle = columnsToHandleList.toArray(new String[0]);

            for (String column : columnsToHandle) {
                data = data.withColumn(column, functions.when(data.col(column).isNull(), 0).otherwise(data.col(column)));
            }

            int maxMissingValues = 2;
            data = data.na().drop(maxMissingValues);
            // Save the processed data to a new CSV file
            String outputCsvPath = "Results//CleanedData.csv";
            data.coalesce(1).write()
                    .option("header", true)
                    .csv(outputCsvPath);

            // Stop the SparkSession
            spark.stop();

            return new OutMessage("Data Cleaned", "No Errors", true);

        } catch (Exception e) {

            return new OutMessage("Data Not Cleaned", e.getMessage(), false);

        }


    }


    private void RenameFile(String outputpath) throws IOException {
        // Replace with your HDFS configuration
        Configuration configuration = new Configuration();
        Path outputCsvPath = new Path(outputpath);

        FileSystem fs = FileSystem.get(configuration);

        // List files in the output directory
        FileStatus[] fileStatuses = fs.listStatus(outputCsvPath);

        // Find the most recent file
        Path latestFile = Arrays.stream(fileStatuses)
                .filter(FileStatus::isFile)
                .max(Comparator.comparingLong(FileStatus::getModificationTime))
                .map(FileStatus::getPath)
                .orElse(null);

        if (latestFile != null) {
            // Rename the most recent file to "result.csv"
            Path dest = new Path(outputCsvPath, "result.csv");
            fs.rename(latestFile, dest);
        }

        fs.close();
    }


    public OutMessage setWordsVector(News news) {
        try {

            wordsVector.RunWord2VecModel(news.getFilePath());

            return new OutMessage("Trainee Data .txt file Created Succssfully", "No Errors", true);

        } catch (Exception e) {

            return new OutMessage("Trainee Data .txt file  Not Created Succssfully", e.getMessage(), false);
        }
    }


    public OutMessage PrepareARFFfile(TraineeData data) {
        try {

            String arffFilePath = "src/main/resources/result/preprocessed_dataset.arff";

            wordsVector.convertCsvToArff(data.getDatapath(), arffFilePath);

            return new OutMessage("ARFF FILE SAVED", "No Errors", true);

        } catch (Exception e) {

            e.getStackTrace();
            e.printStackTrace();

            return new OutMessage("ARFF FILE NOT SAVED", e.getMessage(), false);

        }
    }

    public  double predictLabel(String inputText) {
        // Load the saved model
        String modelPath = "path_to_your_saved_model_directory";
        PipelineModel model = PipelineModel.load(modelPath);

        // Create a Spark session
        SparkSession spark = SparkSession.builder().appName("ModelTesting").getOrCreate();

        // Tokenize input text
        Tokenizer tokenizer = new Tokenizer().setInputCol("input").setOutputCol("words");
        Dataset<Row> inputData = spark.createDataFrame(Collections.singletonList(inputText), String.class)
                .toDF("input");
        inputData = tokenizer.transform(inputData);

        // Remove stopwords
        StopWordsRemover remover = new StopWordsRemover().setInputCol("words").setOutputCol("filtered_words");
        inputData = remover.transform(inputData);

        // Make predictions using the loaded model
        Dataset<Row> predictions = model.transform(inputData);

        // Extract the prediction value
        String predictionVector = predictions.select("prediction").as(Encoders.STRING()).first();

        double prediction = Double.parseDouble(predictionVector);

        // Stop the Spark session
        spark.stop();

        return prediction;

    }



}





