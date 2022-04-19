package by.brel.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Balance implements Serializable {
    private long idBalance;
    private double balance;
    private Customer customer;
    private Tariff tariff;
}
