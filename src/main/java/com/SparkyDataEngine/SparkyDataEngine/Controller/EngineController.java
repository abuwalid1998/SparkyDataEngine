package com.SparkyDataEngine.SparkyDataEngine.Controller;


import com.SparkyDataEngine.SparkyDataEngine.DataServices.DataEngine;
import com.SparkyDataEngine.SparkyDataEngine.Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SparkyEngine")
public class EngineController {


    final DataEngine dataEngine;

    public EngineController(DataEngine dataEngine) {
        this.dataEngine = dataEngine;
    }


    @GetMapping("/GetData")
    public void GetData(){
        System.out.println("Im here >>>> Getting Data");
    }

    @PostMapping("/arrangedata")
    public OutMessage ArrangeData(@RequestBody InputData data){
        System.out.println("I will arrange your Data");
        return dataEngine.ArrangeData(data);
    }

    @PostMapping("/cleandata")
    public OutMessage cleandata(@RequestBody InputData data){
        System.out.println("I will clean your Data");
        return dataEngine.CleanData(data);
    }

    @PostMapping("/arrangeNews")
    public OutMessage arrangeNews(@RequestBody  News InData){
        return dataEngine.ArrangeNews(InData);
    }

    @PostMapping("/wordvector")
    public OutMessage CreateWordVector(@RequestBody  News InData){

        return dataEngine.setWordsVector(InData);
    }

    @PostMapping("/PrepareARFFfile")
    public OutMessage CreateWordVector(@RequestBody TraineeData InData){
        return dataEngine.PrepareARFFfile(InData);
    }

    @PostMapping("/TextClass")
    public OutMessage CreateWordVector(@RequestBody FileToClass data) throws Exception {
        return dataEngine.TextClassification(data);
    }


    @PostMapping("/CreateModel")
    public OutMessage CreateModel(@RequestBody String data) {
        return dataEngine.ClassModel();
    }


}
