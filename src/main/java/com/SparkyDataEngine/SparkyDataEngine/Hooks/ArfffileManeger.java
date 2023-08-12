//package com.SparkyDataEngine.SparkyDataEngine.Hooks;
//
//import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
//import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
//
//import weka.core.Attribute;
//import weka.core.DenseInstance;
//import weka.core.Instances;
//import weka.core.converters.ArffLoader;
//import weka.core.converters.ArffSaver;
//import weka.filters.Filter;
//import weka.filters.unsupervised.attribute.StringToWordVector;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.Collection;
//
//public class ARFFWithWord2Vec {
//
//    public static void convertARFFtoWord2Vec(String arffFilePath, String word2VecModelPath, String outputARFFPath) throws Exception {
//        // Load Word2Vec model
//        WordVectors word2Vec = WordVectorSerializer.readWord2VecModel(new File(word2VecModelPath));
//
//        // Load ARFF dataset
//        ArffLoader loader = new ArffLoader();
//        loader.setSource(new File(arffFilePath));
//        Instances data = loader.getDataSet();
//        data.setClassIndex(data.numAttributes() - 1);
//
//        // Preprocess text data using StringToWordVector
//        StringToWordVector filter = new StringToWordVector();
//        filter.setInputFormat(data);
//        Instances preprocessedData = Filter.useFilter(data, filter);
//
//        // Convert text data to Word2Vec vectors
//        Collection<String> vocab = word2Vec.vocab().words();
//        ArrayList<Attribute> attributes = new ArrayList<>();
//        for (int i = 0; i < word2Vec.getLayerSize(); i++) {
//            attributes.add(new Attribute("word2vec_" + i));
//        }
//        Instances instancesWithWord2Vec = new Instances("word2vec_instances", attributes, preprocessedData.numInstances());
//
//        for (int i = 0; i < preprocessedData.numInstances(); i++) {
//            String text = preprocessedData.instance(i).stringValue(0); // Assuming text is in the first attribute
//            String[] tokens = text.split(" ");
//
//            double[] aggregatedVector = new double[word2Vec.getLayerSize()];
//            for (String token : tokens) {
//                if (vocab.contains(token)) {
//                    double[] vector = word2Vec.getWordVector(token);
//                    for (int j = 0; j < vector.length; j++) {
//                        aggregatedVector[j] += vector[j];
//                    }
//                }
//            }
//
//            for (int j = 0; j < aggregatedVector.length; j++) {
//                aggregatedVector[j] /= tokens.length;
//            }
//
//            DenseInstance newInstance = new DenseInstance(attributes.size());
//            newInstance.setDataset(instancesWithWord2Vec);
//            for (int j = 0; j < aggregatedVector.length; j++) {
//                newInstance.setValue(j, aggregatedVector[j]);
//            }
//            instancesWithWord2Vec.add(newInstance);
//        }
//
//        // Save the new ARFF file
//        ArffSaver saver = new ArffSaver();
//        saver.setInstances(instancesWithWord2Vec);
//        saver.setFile(new File(outputARFFPath));
//        saver.writeBatch();
//    }
//
//    public static void main(String[] args) {
//        try {
//            convertARFFtoWord2Vec("path_to_input.arff", "path_to_word2vec_model.txt", "path_to_output.arff");
//            System.out.println("ARFF file with Word2Vec vectors created successfully.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
