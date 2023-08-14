package com.SparkyDataEngine.SparkyDataEngine.DataServices;

import com.SparkyDataEngine.SparkyDataEngine.Models.InputClassFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DataEngineTest {

    @Autowired
    private DataEngine dataEngine;

    @Test
    @DisplayName("Should throw an exception when the input file is invalid")
    void newsClassWithInvalidInputFile() {
        InputClassFile file = new InputClassFile();
        file.setAppName("TestApp");
        file.setModelPath("models/model");
        file.setDataFile("invalid_file.csv");
        file.setReadHeads(true);
        file.setSelectedColumn("result");
        file.setResultOutPath("results/output.csv");

        OutMessage result = dataEngine.NewsClass(file);

        assertFalse(result.isSuccess());
        assertEquals("Text classification failed", result.getMessage());
    }

    @Test
    @DisplayName("Should throw an exception when the model path is invalid")
    void newsClassWithInvalidModelPath() {
        InputClassFile file = new InputClassFile();
        file.setAppName("TestApp");
        file.setModelPath("invalid/path/to/model");
        file.setDataFile("data.csv");
        file.setReadHeads(true);
        file.setSelectedColumn("result");
        file.setResultOutPath("result.csv");

        OutMessage result = dataEngine.NewsClass(file);

        assertFalse(result.isSuccess());
        assertEquals("Text classification failed", result.getMessage());
        assertNotNull(result.getError());
    }

    @Test
    @DisplayName("Should throw an exception when invalid data is provided")
    void classModelWithInvalidDataThrowsException() {
        try {
            OutMessage result = dataEngine.ClassModel();
            assertFalse(result.isSuccess());
            assertNotNull(result.getErrorMessage());
        } catch (Exception e) {
            fail("An unexpected exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should successfully create a model when valid data is provided")
    void classModelWithValidData() {
        try {
            String arffFilePath = "src/main/resources/result/preprocessed_dataset.arff";
            String outputpath = "src/main/resources/result/preprocessed_dataset_output.arff";

            ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffFilePath);
            Instances data = source.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);

            StringToWordVector filter = new StringToWordVector();
            filter.setInputFormat(data);
            Instances filteredData = Filter.useFilter(data, filter);

            int trainSize = (int) Math.round(filteredData.numInstances() * 0.8);
            int testSize = filteredData.numInstances() - trainSize;

            Instances trainData = new Instances(filteredData, 0, trainSize);
            Instances testData = new Instances(filteredData, trainSize, testSize);

            Classifier classifier = new Logistic();
            classifier.buildClassifier(trainData);
            SerializationHelper.write("trained_model.model", classifier);

            OutMessage result = dataEngine.ClassModel();

            assertTrue(result.isSuccess());
            assertEquals("Model Created Successfully", result.getMessage());

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}