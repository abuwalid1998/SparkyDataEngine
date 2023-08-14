package com.SparkyDataEngine.SparkyDataEngine.Hooks;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Bean
    public SparkSession sparkSession() {
        SparkConf conf = new SparkConf()
                .setAppName("SentimentAnalysis")
                .setMaster("local[*]") // Use more appropriate settings in a production environment
                .set("spark.driver.allowMultipleContexts", "true");

        return SparkSession.builder().config(conf).getOrCreate();
    }
}