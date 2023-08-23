package com.SparkyDataEngine.SparkyDataEngine.Controller;

import com.SparkyDataEngine.SparkyDataEngine.DataServices.DataEngine;
import com.SparkyDataEngine.SparkyDataEngine.DataServices.SentimentAnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/SparkyEngine")
public class EngineController {


    final DataEngine dataEngine;

    final SentimentAnalysisService service;


    public EngineController(DataEngine dataEngine, SentimentAnalysisService service) {
        this.dataEngine = dataEngine;
        this.service = service;
    }


    @GetMapping("/GetData")
    public void GetData() {
        System.out.println("Im here >>>> Getting Data");
    }


    @GetMapping("/Get")
    public void GetModelPrediction() throws Exception {

    }

}

