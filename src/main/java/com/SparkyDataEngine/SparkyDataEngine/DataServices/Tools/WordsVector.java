package com.SparkyDataEngine.SparkyDataEngine.DataServices.Tools;


import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.stemmers.SnowballStemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;
import java.util.*;


@Service
public class WordsVector {

    private  List<String> readCSVAndPreprocess(String filePath) throws IOException {
        List<String> sentences = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length >= 3) {
                    String pubDate = nextLine[1];  // Assuming pubDate is in the second column
                    String title = nextLine[0];    // Assuming title is in the first column
                    String description = nextLine[4];  // Assuming description is in the fifth column

                    // Combine pubDate, title, and description into a single sentence
                    String sentence = pubDate + " " + title + " " + description;
                    // Perform preprocessing on 'sentence' if needed
                    sentences.add(sentence);
                }
            }
        }

        return sentences;
    }

    private  void trainWord2VecModel(List<String> sentences) throws IOException {
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        CollectionSentenceIterator sentenceIterator = new CollectionSentenceIterator(sentences);

        int vectorSize = 100;
        int windowSize = 5;
        int iterations = 5;
        int minWordFrequency = 5;

        org.deeplearning4j.models.word2vec.Word2Vec vec = new org.deeplearning4j.models.word2vec.Word2Vec.Builder()
                .minWordFrequency(minWordFrequency)
                .iterations(iterations)
                .layerSize(vectorSize)
                .windowSize(windowSize)
                .iterate(sentenceIterator)
                .tokenizerFactory(tokenizerFactory)
                .build();

        vec.fit();

        WordVectorSerializer.writeWordVectors(vec, "word2vec_model_from_csv.txt");

    }


    public void RunWord2VecModel(String filepath) throws IOException {

         List<String>  list =readCSVAndPreprocess(filepath);

         trainWord2VecModel(list);



    }



    public void TextClassification(String trainDataFilePath) {
        // Load the pre-trained Word2Vec model
        WordVectors word2Vec = loadWord2VecModel("word2vec_model_from_csv.txt");

        // Load the ARFF training dataset
        Instances trainingData = loadArffDataset(trainDataFilePath);

        // Build and evaluate a classifier (NaiveBayes for example)
        Classifier classifier = new NaiveBayes();

        try {
            classifier.buildClassifier(trainingData);

            // Evaluate the classifier
            Evaluation eval = new Evaluation(trainingData);
            eval.crossValidateModel(classifier, trainingData, 10, new Random(1));

            System.out.println(eval.toSummaryString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WordVectors loadWord2VecModel(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (WordVectors) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Instances loadArffDataset(String filePath) {
        try {
            ArffLoader loader = new ArffLoader();
            loader.setFile(new File(filePath));
            return loader.getDataSet();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void convertCsvToArff(String csvFilePath, String arffFilePath) throws IOException {

        // Load and process CSV data
        CSVParser csvParser = CSVFormat.DEFAULT.parse(new FileReader(csvFilePath));

        FastVector<Attribute> attributes = new FastVector<>(2);
        attributes.addElement(new Attribute("text", (FastVector<String>) null));
        FastVector<String> classLabels = new FastVector<String>(3);
        classLabels.addElement("positive");
        classLabels.addElement("neutral");
        classLabels.addElement("negative");
        attributes.addElement(new Attribute("class", classLabels));

        Instances instances = new Instances("sentiment_analysis", attributes, 0);
        Set<String> uniqueInstances = new HashSet<>(); // To track unique instances

        for (CSVRecord record : csvParser) {
            String sentiment = record.get(0); // Assuming sentiment is the first column
            String text = record.get(1); // Assuming text is the second column

            String instanceString = sentiment + text;
            if (!uniqueInstances.contains(instanceString)) {
                double[] instanceValues = new double[2];
                instanceValues[0] = instances.attribute(0).addStringValue(text);
                instanceValues[1] = classLabels.indexOf(sentiment);

                instances.add(new DenseInstance(1.0, instanceValues));
                uniqueInstances.add(instanceString);
            }
        }

        csvParser.close();

        // Save ARFF file
        FileWriter writer = new FileWriter(arffFilePath);
        writer.write(instances.toString());
        writer.close();

        System.out.println("ARFF file saved: " + arffFilePath);
    }



    public void convertToNumeric(String arffFilePath, String outputArffFilePath) throws Exception {
        // Load ARFF file
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(arffFilePath));
        Instances data = loader.getDataSet();

        // Preprocess text
        StringToWordVector filter = new StringToWordVector();
        filter.setInputFormat(data);

        filter.setStemmer(new SnowballStemmer());
        filter.setWordsToKeep(1000); // Set the number of words to keep
        Instances preprocessedData = Filter.useFilter(data, filter);

        // Save preprocessed data to output ARFF file
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputArffFilePath));
        writer.write(preprocessedData.toString());
        writer.flush();
        writer.close();
    }





}
