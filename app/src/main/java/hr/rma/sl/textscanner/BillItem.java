package hr.rma.sl.textscanner;

import java.io.Serializable;

public class BillItem implements Serializable {
    private String name;
    private String cost;
    private String amount;

    protected BillItem(){

    }

    protected BillItem(String name, String cost)
    {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String toString(){
        return this.name + "**" + this.cost;
    }

}
