package com.SparkyDataEngine.SparkyDataEngine.DataServices;


import com.SparkyDataEngine.SparkyDataEngine.Models.FileToClass;
import com.SparkyDataEngine.SparkyDataEngine.Models.InputData;
import com.SparkyDataEngine.SparkyDataEngine.Models.News;
import com.SparkyDataEngine.SparkyDataEngine.Models.OutMessage;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class DataEngine {

    //this Class Should Arrange the Data and gives the right format for data to analyze and Clean it , handle missing values
    //the data is in .CSV file , Sql database , External API'S
    //Example to import .CSV file and Arrange the data form Large to small businesses


    public DataEngine() {

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


    public OutMessage ArrangeNews(News news) {


        try {

            SparkSession spark = SparkSession.builder()
                    .appName(news.getAppName())
                    .master(news.getSparkConfig().getHost())
                    .getOrCreate();

            Dataset<Row> data = spark.read()
                    .option("header", true)
                    .csv(news.getFilePath());

            List<String> columnsToHandleList = news.getHeaders();

            String[] columnsToHandle = columnsToHandleList.toArray(new String[0]);

            for (String column : columnsToHandle) {
                data = data.withColumn(column, functions.when(data.col(column).isNull(), 0).otherwise(data.col(column)));
            }

            int maxMissingValues = 2;

            data = data.na().drop(maxMissingValues);
            // Save the processed data to a new CSV file
            String outputCsvPath = "Results//CleanedData";

            data.coalesce(1).write()
                    .option("header", true)
                    .csv(outputCsvPath);


            RenameFile(outputCsvPath);
            // Stop the SparkSession
            spark.stop();

            return new OutMessage("News Arranged And Cleaned Successfully", "No Errors", true);

        } catch (Exception e) {

            return new OutMessage("News Not Arranged", e.getMessage(), false);

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



    public OutMessage TextClassification(FileToClass file) throws Exception {

        try {

            // Load the pre-trained classifier model
            Classifier classifier = (Classifier) SerializationHelper.read("result/preprocessed_dataset.arff");

            // Load the ARFF file for attribute configuration
            BufferedReader arffReader = new BufferedReader(new FileReader("result/preprocessed_dataset.arff"));
            Instances attributeStructure = new Instances(arffReader);
            arffReader.close();

            // Read CSV file and process data
            BufferedReader csvReader = new BufferedReader(new FileReader(file.getFilepath()));
            String line;
            ArrayList<String> results = new ArrayList<>();

            while ((line = csvReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    // Get the index of the column containing the text to classify
                    int columnIndex = attributeStructure.attribute(file.getTargetHeader()).index();

                    // Create an instance and set the text attribute
                    Instance instance = new DenseInstance(attributeStructure.numAttributes());
                    instance.setDataset(attributeStructure);
                    instance.setValue(columnIndex, parts[columnIndex]);

                    // Classify the instance
                    double prediction = classifier.classifyInstance(instance);
                    String predictedClass = attributeStructure.classAttribute().value((int) prediction);

                    // Store the result
                    results.add(predictedClass);
                }
            }

            csvReader.close();

            // Save the results to a new CSV file
            FileWriter writer = new FileWriter("src/main/resources/result/results.csv");
            writer.write("Predicted Class\n");
            for (String result : results) {
                writer.write(result + "\n");
            }
            writer.close();
            return new OutMessage("File Classified", "", true);
        }

    catch(Exception e)
    {
        return new OutMessage("File Not Classified", e.getMessage(), false);
    }
}



    }





