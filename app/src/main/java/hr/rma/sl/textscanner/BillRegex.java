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
    private Bill b;

    public void generateBillData(SparseArray<TextBlock> textBlocks){
        String imageText = "";
        String fullText = "";
        ArrayList <BillItem> items= new ArrayList<>();
        int amountCnt=0, nameCnt=0, priceCnt=0;
        for (int i = 0; i < textBlocks.size(); i++)
        {
            //ako nađemo cijenu ne idemo dalje jer to može poremetiti amountCnt
            boolean priceFound = false;

            TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
            imageText = textBlock.getValue();                   // return string
            //kol i naziv su u svakom računu
            if(imageText.contains("Kol.")) imageText.replaceAll("Kol.", "");
            if(imageText.contains("Naziv")) imageText.replaceAll("Naziv", "");
            Log.d("tag", "ovo je" + imageText);
            fullText += imageText;

            //regex cijene, broj s dvije decimale iza
            Pattern p = Pattern.compile("\\d+,\\d{2}\\s[A-Z]{1}");
            Matcher m = p.matcher(imageText);
            while(m.find()) {
                //double zahtjeva točku ispred decimala, u konkretom računu su odvojeni decimalom pa to moramo izmjeniti
                Double cijena = Double.parseDouble(m.group(0).replaceAll("," , ".").replaceAll("[A-z]", ""));
                if(priceCnt >= items.size())
                {
                   items.add(new BillItem("", 0, cijena));
                }else{
                   items.get(priceCnt).setCost(cijena);
                }
                Log.d("cijena " ,"Cijena:" + m.group(0) + "  index: " + items.get(priceCnt));
                priceCnt++;
                priceFound = true;
            }
            //kraj regexa
            //ako je cijenan nađena idi na iduci dio teksta
            if(priceFound == true) continue;

            //ako imamo više linija u tekstu razdvojimo ih
            String lines[] = imageText.split("\\r?\\n");
            String name = "";
            //neki itemi su u dva reda i nemaju količinu, njima je količina 1
            boolean dvoredni = false;
            for(int z = 0; z < lines.length;z++)
            {
                //if(lines[z].contains(" "))  Log.d("kol","lines  " + lines[z].substring(lines[z].indexOf(" "), lines[z].length()) + "*** " + lines[z] + "**" + lines[z].substring(0, lines[z].indexOf(" ")));
                //ako imamo količinu u tekstu ona je na početku i odmaknuta je od ostatka sa spaceom
                if(!dvoredni && lines[z].contains(" "))
                {
                    int kolicina = 0;
                    try {
                        String s = lines[z].substring(0, lines[z].indexOf(" ")).replaceAll("\\s", "");
                        Log.d("string", "Str:" + s);
                        kolicina = Integer.parseInt(s);
                        if(amountCnt >= items.size())
                        {
                            items.add(new BillItem("", 0 ,0));
                            Log.d("tag", "Dodan novi");
                        }
                        items.get(amountCnt).setAmount((double)kolicina);
                        items.get(amountCnt).setName(lines[z].substring(lines[z].indexOf(" "), lines[z].length()));
                        //nameCnt++;
                        amountCnt++;
                        Log.d("kol", "Uhvatio sam količinu: " + kolicina);
                    }catch(NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                    //ako u stringu nije bilo količine onda imamo item sa dvorednim imenom
                    if(kolicina == 0){
                        name += lines[z];
                        Log.d("kol", "Tu nema int " + name);
                        dvoredni = true;
                    }
                }else{
                    name += lines[z];
                    if(amountCnt >= items.size())
                    {
                        items.add(new BillItem("", 0 ,0));
                        Log.d("tag", "Dodan novi");
                    }
                    dvoredni = false;
                    items.get(amountCnt).setName(name);
                    name = "";
                    amountCnt++;
                }

              /*  if(m.find())
                {
                    //if(amountCnt < items.size()) items.get(amountCnt).setName(name);
                    continue;
                }
                /*if(lines[z].contains(","))
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
                        items.get(amountCnt).setName(name);
                        name = "";
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
                        items.get(amountCnt).setName(name);
                        name = "";
                        Log.d("kol" ,"Količina:" + m.group(0) + "  index: " + amountCnt + "nije zarez");
                        amountCnt++;
                        //imageText = imageText.replace(m.group(0), "");
                        break;
                    }
                    //items.get(amountCnt).setName(lines[z]);
                }*/
            }
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
        b = new Bill(items, format);
        Log.d("item", "Ukupno: " + format);
        Log.d("racun", fullText + "PriceCnt:" + priceCnt + "AmountCnt:" +amountCnt);
    }

    public Bill getBill() {
        return b;
    }

}
