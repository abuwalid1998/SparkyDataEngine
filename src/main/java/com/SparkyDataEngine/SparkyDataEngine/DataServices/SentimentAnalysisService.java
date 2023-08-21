package com.SparkyDataEngine.SparkyDataEngine.DataServices;

import org.nd4j.shade.protobuf.InvalidProtocolBufferException;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;
import org.tensorflow.framework.MetaGraphDef;
import org.tensorflow.framework.SignatureDef;

import org.tensorflow.TensorFlowException;
import org.tensorflow.framework.TensorInfo;

import java.util.Map;


import org.springframework.stereotype.Service;

@Service
public class SentimentAnalysisService {
    private final String modelPath = "C:\\Users\\PC\\OneDrive\\Desktop\\SparkyDataEngine\\TrainedModels\\saved_model"; // Path to the SavedModel directory
    private final String servingDefaultSignature = "serving_default"; // Use the available signature name

    private final SavedModelBundle model = SavedModelBundle.load(modelPath, "serve");

    public double predictSentiment(String text) {
        Session.Runner runner = null;
        try (Tensor<String> inputTensor = Tensors.create(text)) {
            runner = model.session().runner();

            MetaGraphDef metaGraphDef = MetaGraphDef.parseFrom(model.metaGraphDef());
            SignatureDef signatureDef = metaGraphDef.getSignatureDefOrThrow(servingDefaultSignature);

            String inputTensorName = "embedding_input:0"; // Update with the actual input tensor name

            runner.feed(inputTensorName, inputTensor);

            // Fetch output tensors
            for (Map.Entry<String, TensorInfo> output : signatureDef.getOutputsMap().entrySet()) {
                String outputTensorName = output.getValue().getName();
                runner.fetch(outputTensorName);
            }

            try (Tensor<?> outputTensor = runner.run().get(0)) {
                float[] predictions = new float[(int) outputTensor.shape()[0]];
                outputTensor.copyTo(predictions);
                return predictions[0];
            } catch (Exception e) {
                e.printStackTrace();
                return -1.0; // Indicate error
            }
        } catch (TensorFlowException | InvalidProtocolBufferException e) {
            e.printStackTrace();
            return -100.0; // Indicate error
        }
    }
}
