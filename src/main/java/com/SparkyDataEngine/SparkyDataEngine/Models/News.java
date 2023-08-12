package com.SparkyDataEngine.SparkyDataEngine.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class News {

    private SparkConfig sparkConfig;

    private List<String> headers;

    private String filePath;

    private  String appName;

    private boolean readHeaders;

}
