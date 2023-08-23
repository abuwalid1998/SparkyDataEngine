package com.SparkyDataEngine.SparkyDataEngine.DataServices;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.SparkyDataEngine.SparkyDataEngine.Models.News;
import com.SparkyDataEngine.SparkyDataEngine.Models.OutMessage;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

@Service
public class DataEngine {

    //this Class Should Arrange the Data and gives the right format for data to analyze and Clean it , handle missing values
    //the data is in .CSV file , Sql database , External API'S
    //Example to import .CSV file and Arrange the data form Large to small businesses


    public OutMessage ArrangeNews(News news) {
        return new OutMessage();

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


    public OutMessage TextClassification(News news) throws Exception {


        String modelPath = "C:\\Users\\PC\\OneDrive\\Desktop\\SparkyDataEngine\\TrainedModels\\text_classification_model"; // Replace with your Word2Vec model path
        String newsSentence = news.getTitle(); //news String

        WordVectors wordVectors = WordVectorSerializer.readWord2VecModel(modelPath);

        // Preprocess the news sentence
        String[] words = newsSentence.split(" ");
        INDArray sentenceVec = Nd4j.zeros(100); // Assuming word vectors are of length 100

        for (String word : words) {
            INDArray vector = wordVectors.getWordVectorMatrix(word);
            if (vector != null) {
                sentenceVec.addi(vector);
            }
        }

        // Load your trained classification model (example: simple neural network)
        Model model = ModelSerializer.restoreMultiLayerNetwork(modelPath);

        double predictedLabel = model.score();


        return new OutMessage();
    }








}





