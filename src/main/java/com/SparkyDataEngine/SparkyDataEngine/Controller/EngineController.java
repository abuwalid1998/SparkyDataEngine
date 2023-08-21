package com.SparkyDataEngine.SparkyDataEngine.Controller;


import com.SparkyDataEngine.SparkyDataEngine.DataServices.DataEngine;
import com.SparkyDataEngine.SparkyDataEngine.DataServices.SentimentAnalysisService;
import com.SparkyDataEngine.SparkyDataEngine.Models.*;
import org.springframework.web.bind.annotation.*;

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
    public void GetData(){
        System.out.println("Im here >>>> Getting Data");
    }


    @PostMapping("/CleanData")
    public void CleanData(@RequestParam("data") String data) {
        dataEngine.CleanDataFile(data);
    }


    @PostMapping("/Classification")
    public double Classifications(@RequestBody News article) throws Exception {
        return service.predictSentiment(article.getTitle());
    }

}
