package by.brel.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Tariff implements Serializable {
    private long idTariff;
    private String title;
}
