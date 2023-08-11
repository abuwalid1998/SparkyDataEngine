package com.SparkyDataEngine.SparkyDataEngine.DataServices;


import com.SparkyDataEngine.SparkyDataEngine.Models.InputData;
import com.SparkyDataEngine.SparkyDataEngine.Models.OutMessage;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataEngine {

    //this Class Should Arrange the Data and gives the right format for data to analyze and Clean it , handle missing values
    //the data is in .CSV file , Sql database , External API'S
    //Example to import .CSV file and Arrange the data form Large to small businesses

    public OutMessage ArrangeData(InputData data){


        try {

            System.out.println(">>>>>>>>>>>>>>>>>"+data.getSparkConfig());

            SparkSession  spark = SparkSession.builder()
                    .appName(data.getAppName())
                    .master(data.getSparkConfig())
                    .getOrCreate();

            // Load the financial data from a CSV file into a DataFrame
            Dataset<Row> financialData = spark.read()
                    .option("header", data.isReadHeaders())
                    .csv(data.getFilePath());


            if (data.getFunction() == 1){
                Dataset<Row>  sortedData = financialData.select(data.getName(), data.getValueCol())
                        .orderBy(functions.col(data.getValueCol()).desc());
                sortedData.show();

                String outputCsvPath = "Results//ARRANGED_MYCSVFILENAME.csv";

                sortedData.coalesce(1).write()
                        .option("header", true)
                        .csv(outputCsvPath);


            }else{
                Dataset<Row>  sortedData = financialData.select(data.getName(), data.getValueCol())
                        .orderBy(functions.col(data.getValueCol()).asc());
                sortedData.show();

            }

            // Stop the SparkSession
            spark.stop();

            return new OutMessage("Data Arranged Succssfully","No Errors Found",true);

        }catch (Exception e) {

            return new OutMessage("Data Not Arranged Error Found", e.getMessage(), false);

        }
    }

    public OutMessage CleanData(InputData indata) {
        try {


            SparkSession  spark = SparkSession.builder()
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

            return new OutMessage("Data Cleaned","No Errors",true);

        }catch (Exception e){

        return new OutMessage("Data Not Cleaned",e.getMessage(),false);

        }




    }








}
