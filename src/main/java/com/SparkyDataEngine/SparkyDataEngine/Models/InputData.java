package com.SparkyDataEngine.SparkyDataEngine.Models;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InputData {

    private  String appName;

    private  String sparkConfig;

    private boolean readHeaders;

    private String filePath;

    private String name;

    private String valueCol;

    private int function;

}
