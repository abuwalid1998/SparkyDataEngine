package com.SparkyDataEngine.SparkyDataEngine.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutMessage {

    private String message;
    private String errormsg;
    private boolean isDone;

}
