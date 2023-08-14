package com.SparkyDataEngine.SparkyDataEngine.Controller;


import com.SparkyDataEngine.SparkyDataEngine.DataServices.DataEngine;
import com.SparkyDataEngine.SparkyDataEngine.Models.*;
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
    public boolean cleandata(@RequestBody InputData data){

        System.out.println("I will clean your Data");

        return dataEngine.CleanDataFile(data.getFilePath());

    }


}
