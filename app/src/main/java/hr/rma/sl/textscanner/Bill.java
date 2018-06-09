package hr.rma.sl.textscanner;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Bill implements Serializable{
    private List<BillItem> items = new ArrayList<BillItem>();
    private String total;

    public Bill()
    {
        this.setItems(null);
      //  this.setTotal("0.0");
    }
    public Bill(List<BillItem> items, String total)
    {
        this.setItems(items);
        this.setTotal(total);
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void addItem(BillItem item) {
        this.items.add(item);
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public HashMap<String, String> createHashMap(){
        HashMap<String, String> dokument = new HashMap<>();

        // adding each child node to HashMap key => value
        //    dokument.put("id", this.getId());
        dokument.put("items", "Artikl: " + this.getItems());
        dokument.put("total", "Ukupno " + this.getTotal());
        return dokument;
    }

    public String toString(){
        return "**" + this.total;
    }

}
