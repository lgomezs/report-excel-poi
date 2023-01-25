package com.example.demopoi.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserExportData implements Serializable {
    
    private String firstName;
    private String lastName;
    private Integer code;
    private String address;
    private String email;
       
    
}
