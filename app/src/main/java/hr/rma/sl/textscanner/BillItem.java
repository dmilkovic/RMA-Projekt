package hr.rma.sl.textscanner;

import java.io.Serializable;

public class BillItem implements Serializable {
    private String name;
    private double cost;
    private double amount;


    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }



    protected BillItem(){

    }

    protected BillItem(String name, double cost)
    {
        this.name = name;
        this.cost = cost;
    }

    protected BillItem(String name, double amount, double cost)
    {
        this.name = name;
        this.amount = amount;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String toString(){
        return this.name + "**" + this.cost;
    }

}
