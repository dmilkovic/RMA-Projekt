package hr.rma.sl.textscanner;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.text.TextBlock;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillRegex {
    private String name, amount, price;
    private Boolean side2Flag=false;
    private int nameCnt = 0;

    public void generateBillData(SparseArray<TextBlock> textBlocks){
        String imageText = "";
        String fullText = "";
        ArrayList <BillItem> items= new ArrayList<>();
        int amountCnt=0, nameCnt=0, priceCnt=0;
        boolean surnameFlag = false, nameFlag = false, nameFlag1 = false;
        for (int i = 0; i < textBlocks.size(); i++)
        {

            TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
            imageText = textBlock.getValue();                   // return string
            if(imageText.contains("Kol.")) imageText.replaceAll("Kol.", "");
            if(imageText.contains("Naziv")) imageText.replaceAll("Naziv", "");
            Log.d("tag", "ovo je" + imageText);

            /*if(imageText.contains("[0-9.]"))
            {
                if(items.size() < priceCnt+1)
                {
                    items.add(priceCnt, new BillItem(" ", 0,  Double.parseDouble(imageText)));
                    priceCnt++;
                }else{ items.get(priceCnt).setCost(Double.parseDouble(imageText));
                }
                Log.d("tag", "Cijena artikla:" + imageText);
            }*/
            Pattern p = Pattern.compile("\\d+,\\d{2}\\s[A-Z]{1}");
            Matcher m = p.matcher(imageText);
            while(m.find()) {
                Double cijena = Double.parseDouble(m.group(0).replaceAll("," , ".").replaceAll("[A-z]", ""));
                if(priceCnt >= items.size())
                {
                   items.add(new BillItem("", 0, cijena));
                }else{
                   items.get(priceCnt).setCost(cijena);
                }
                Log.d("cijena " ,"Cijena:" + m.group(0) + "  index: " + items.get(priceCnt));
                priceCnt++;
                imageText = "";
            }
            imageText = imageText.replaceAll("\\b\\d+,\\d{2}\\b", "");
            Log.d("tag", "ovo je nakon promjene" + imageText);
            String lines[] = imageText.split("\\r?\\n");
            for(int z = 0; z < lines.length;z++)
            {
                //vidi ima li što sa kg u računu, ako da kreni s iducom stvari u petlji
                p = Pattern.compile("\\b\\d+,\\d{3}\\b");
                m = p.matcher(lines[z]);
                if(m.find()) continue;

                if(lines[z].contains(","))
                {
                    Log.d( "tag","check: " + lines[z].substring(0,lines[z].indexOf(",")));
                    p = Pattern.compile("\\b\\d+\\b");
                    m = p.matcher(lines[z].substring(0,lines[z].indexOf(",")));
                    while (m.find() )
                    {
                        // Log.d("tag", "Podijeljen na:" + lines[z]);
                        if(amountCnt >= items.size())
                        {
                            items.add(new BillItem("", 0 ,0));
                        }
                        items.get(amountCnt).setAmount(Double.parseDouble(m.group(0)));
                        items.get(amountCnt).setName(lines[z].substring(m.group(0).length(),lines[z].length()));
                        Log.d("kol" ,"Količina:" + m.group(0) + "  index: " + amountCnt + "zarez");
                        amountCnt++;
                        break;
                    }
                    //items.get(amountCnt).setName(lines[z]);
                }else{
                    Log.d( "tag","check: " + lines[z]);
                    p = Pattern.compile("\\b\\d+\\b");
                    if(lines[z].length() > 2)m = p.matcher(lines[z].substring(0,3));
                    else m = p.matcher(lines[z]);
                    while (m.find() )
                    {
                        if(amountCnt >= items.size())
                        {
                            items.add(new BillItem("", 0 ,0));
                        }
                        items.get(amountCnt).setAmount(Double.parseDouble(m.group(0)));
                        items.get(amountCnt).setName(lines[z].substring(m.group(0).length(),lines[z].length()));
                        Log.d("kol" ,"Količina:" + m.group(0) + "  index: " + amountCnt + "nije zarez");
                        amountCnt++;
                        //imageText = imageText.replace(m.group(0), "");
                        break;
                    }
                    //items.get(amountCnt).setName(lines[z]);
                }
            }
            fullText += imageText;
          }
        double total = 0;
        for(int i = 0; i < items.size(); i++)
        {
            if(items.get(i).getAmount() == 0.0) items.get(i).setAmount(1);
            total += items.get(i).getAmount() * items.get(i).getCost();
            Log.d("item", items.get(i).toString());
        }
        String pattern = "#.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        String format = decimalFormat.format(total);
        Log.d("item", "Ukupno: " + format);
        Log.d("racun", fullText);
    }
/*
    public String getBirthday() {
        return birthday;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public String getDateOfIssue() {
        return dateOfIssue;
    }

    public String getOIB() {
        return OIB;
    }

    public String getIme() {
        return ime;
    }

    public String getSpol() {
        return spol;
    }

    public String getAddress() {
        return address;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public Boolean getSide2Flag() {
        return side2Flag;
    }

    public int getNameCnt() {
        return nameCnt;
    }*/
}
