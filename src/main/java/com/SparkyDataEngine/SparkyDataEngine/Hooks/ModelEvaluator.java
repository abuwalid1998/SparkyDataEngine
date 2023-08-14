package com.SparkyDataEngine.SparkyDataEngine.Hooks;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;

public class ModelEvaluator {

    public void Evaluator(String DataSource, String modelpath) {
        try {
            // Load the trained model
            Classifier classifier = (Classifier) SerializationHelper.read(modelpath);

            // Load new data from CSV for evaluation
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(DataSource));
            Instances newNewsData = loader.getDataSet();
            newNewsData.setClassIndex(newNewsData.numAttributes() - 1);

            // Apply the same preprocessing steps as during training
            StringToWordVector filter = new StringToWordVector();
            filter.setInputFormat(newNewsData);
            Instances preprocessedData = Filter.useFilter(newNewsData, filter);

            // Evaluate the model
            int correctPredictions = 0;
            for (int i = 0; i < preprocessedData.numInstances(); i++) {
                Instance instance = preprocessedData.instance(i);
                double predictedClass = classifier.classifyInstance(instance);
                double actualClass = instance.classValue();

                if (predictedClass == actualClass) {
                    correctPredictions++;
                }
            }

            double accuracy = (double) correctPredictions / preprocessedData.numInstances();
            System.out.println("Accuracy: " + accuracy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}