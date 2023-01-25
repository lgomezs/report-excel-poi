package com.example.demopoi.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Miguel Gomez Saavedra.
 */
@Getter
@Setter
public class ParameterDTO implements Serializable {

    private static final long serialVersionUID = -2686894494075592137L;

    private Integer row;

    private Integer column;

    private Object value;

}
