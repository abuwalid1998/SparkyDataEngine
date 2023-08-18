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


}
