package com.SparkyDataEngine.SparkyDataEngine.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InputClassFile {

    private String appName;

    private String modelPath;

    private String dataFile;

    private String readHeads;
    // "true","false"

    private String selectedColumn;

    private String resultOutPath;

}
