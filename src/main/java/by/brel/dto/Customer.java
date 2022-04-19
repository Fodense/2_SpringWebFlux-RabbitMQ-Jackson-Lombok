package by.brel.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Customer implements Serializable {
    private long idCustomer;
    private String firstName;
    private String lastName;
    private String mobilePhone;
}
