package com.SparkyDataEngine.SparkyDataEngine.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileToClass {

    private String filepath;
    private String targetHeader;

}
