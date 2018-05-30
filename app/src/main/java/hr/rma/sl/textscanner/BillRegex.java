package hr.rma.sl.textscanner;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.text.TextBlock;

import java.text.DateFormat;
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
            Log.d("tag", "ovo je" + imageText +imageText.contains("[A-Z]") );
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

            Pattern p = Pattern.compile("\\d+,\\d{2}");
            Matcher m = p.matcher(imageText);
            while(m.find()) {
                Log.d("cijena " ,"Cijena:" + m.group(0) + "  index: " + priceCnt);
                priceCnt++;
            }
            imageText = imageText.replaceAll("\\d+,\\d{2}", "");

            String lines[] = imageText.split("\\r?\\n");
            for(int z = 0; z < lines.length;z++)
            {
                Log.d("tag", "Podijeljen na:" + lines[z]);
                if(lines[z].contains(","))
                {
                    Log.d( "tag","check: " + lines[z].substring(0,lines[z].indexOf(",")));
                    p = Pattern.compile("\\d+");
                    m = p.matcher(lines[z].substring(0,lines[z].indexOf(",")));
                    while (m.find()){
                        Log.d("kol" ,"Količina:" + m.group(0) + "  index: " + amountCnt);
                        amountCnt++;
                    }
                }else{
                    p = Pattern.compile("\\d+");
                    m = p.matcher(lines[z]);
                    while (m.find()){
                        Log.d("kol" ,"Količina:" + m.group(0) + "  index: " + amountCnt);
                        amountCnt++;
                    }
                }
            }
           /* if(m.find())
            {
                Log.d("kol " ,"Količina:" + m.group(0) + "  index: " + amountCnt);
                amountCnt++;
                continue;
            }*/


           // imageText = imageText.replaceAll("[0-9]+\\.\\[0-9]{2}", "cijena");

            // myText.append(imageText);
           /* if(imageText.contains("I0HRV") || imageText.contains("<"))
            {
                side2Flag = true;
                continue;
            }
            // if(imageText.contains("RH REPUBLIC REPUBLIKA OF HRVATSKA CROATIA")) continue;
            // imageText =  imageText.replaceAll("[a-z]", "");

            /*else if(imageText.contains("Ime/Name"))
            {
                nameFlag1 = true;
                continue;
            }

          /* if(surnameFlag && !nameFlag)
            {
                prezime = imageText;
                nameFlag = true;
                continue;
            }*/
           /*if(!side2Flag)
            {
                if(imageText.contains("M/M"))
                {
                    spol = imageText.substring(imageText.indexOf("M/M"),imageText.indexOf("M/M")+3);
                }else if(imageText.contains("Ž/F"))
                {
                    spol = imageText.substring(imageText.indexOf("Ž/F"),imageText.indexOf("Ž/F")+3);
                }
                imageText = imageText.replaceAll("[A-Z][a-z]", "");
                imageText = imageText.replaceAll("[a-z]", "");
                imageText = imageText.replaceAll("OSOBNA", "");
                imageText = imageText.replaceAll("ISKAZNICA", "");
                imageText = imageText.replaceAll("REPUBLIC", "");
                imageText = imageText.replaceAll("REPUBLIKA", "");
                imageText = imageText.replaceAll("IDENTITY", "");
                imageText = imageText.replaceAll("DENTITY", "");
                imageText = imageText.replaceAll("CARD", "");
                imageText = imageText.replaceAll("HRVATSKA", "");
                imageText = imageText.replaceAll("CROATIA", "");
                imageText = imageText.replaceAll("\\bHRV\\b", "");
                imageText = imageText.replaceAll("\\bRH\\b", "");
                imageText = imageText.replaceAll("\\bOF\\b", "");
                imageText = imageText.replaceAll("/", "");
                String check = getDate(imageText, "name");
                if(check.length() > 2 && nameCnt<2)
                {
                    Log.d("tag", "check:" + getDate(imageText, "name"));
                    if(ime.contains(check)) continue;

                    ime += " "+ getDate(imageText, "name");
                    //nameCnt++;
                }
            }*/
            fullText += imageText;
           /* Log.d("tag", "Ovo je" + imageText + side2Flag);
        }*/

       /* if(side2Flag)
        {
            getDates(fullText, "dateOfIssue");
           /* if(fullText.contains("Prebiv"))
            {
              //  if(fullText.indexOf("Prebiv") - 11 > -1) getDates((fullText.substring(fullText.indexOf("Prebiv") - 11, fullText.indexOf("Prebiv"))), "OIB");
                //if(fullText.contains("OIB/Personal identification number") || fullText.contains("OI8/Personal identification number") || fullText.contains("OIE/Personal identification number")|| fullText.contains("OIBPersonal identification number")) getDates(fullText.substring(fullText.indexOf("OIB/Personal identification number")+"OIB/Personal identification number".length(), fullText.length()), "OIB");
                getDates(fullText, "OIB");
            }*/
          //  getDates(fullText, "OIB");
            // if(fullText.contains(fullText.substring(fullText.indexOf("Residence")+"Residence".length(), fullText.indexOf("zdala"))))
           /* if(fullText.indexOf("Residence") < fullText.indexOf("zdala"))
            {
                Log.d("tag", fullText.indexOf("Residence") +" "+ fullText.indexOf("zdala"));
                address = fullText.substring(fullText.indexOf("Residence")+"Residence".length(), fullText.indexOf("zdala")).replaceAll("[A-Z][a-z]", "").replaceAll("[a-z]", "").replaceAll(dateOfIssue+".", "").replaceAll(OIB, "").replaceAll("\bOIB\b", "");
            }
            Log.d("side2", fullText + "***" + dateOfIssue + side2Flag + "**OIB: " + OIB + "** adresa:" + address);
            //  Log.d("address", "adresa:" + address);
        */}
        Log.d("racun", fullText);
    }

    /*private void getDates(String fullText, String type)
    {
        List<String> dates = new ArrayList<>();
        int len = 10, i  = 0, cnt = 0;
        if(type.toLowerCase().equals("documentnumber")) len = 9;
        if(type.toLowerCase().equals("oib")) len = 11;
        while(len+i <= fullText.length()) {
            String check = fullText.substring(i, len + i);
            //   if(check.contains(s1)) cnt++;
            String date1 = new String();
            if (type.equals("date")) {
                date1 = getDate(check, "date");
                if (date1.length() == 10) {
                    Log.d("side2", "datum6" + getDate(check, "date") + dates.toString());
                    dates.add(date1);
                }
            }
            else if(type.equals("documentnumber"))
            {
                String str = getDate(check, "documentnumber");
                if(str.length() == 9) {
                    documentNumber = str;
                    return;
                }
            }else if(type.equals("dateOfIssue")){
                String str = getDate(check, "date");
                if(str.length() == 10)
                {
                    //Log.d("side2", "datum" + getDate(check, "date"));
                    dateOfIssue = str;
                    return;
                }
            }else if(type.equals("OIB"))
            {
                // Log.d("side2", "check" + check);
                // Log.d("side2", "OIB" + getDate(check, "OIB"));
                String str = getDate(check, "OIB");
                if(str.length() == 11)
                {
                    //Log.d("side2", "OIB" + getDate(check, "OIB"));
                    OIB = str;
                    return;
                }
            }
            //ima li string 11 znakova(koliko ima i datum
            i++;
        }
        //  Log.d("side2", "gtzf"  +dates.size() + dates.get(0) + dates.get(1));
        //usporedi datume i vidi koji je rođendan
        if(dates.size() >1 ) {
            try {
                //DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy.");
                Date tempDate = null;
                Date tempDate1 = null;
                dates.set(0, dates.get(0) + ".");
                dates.set(1, dates.get(1) + ".");
                Log.d("side2", "gtzf"  +dates.size() + dates.toString());
                tempDate = format.parse(dates.get(0));
                tempDate1 = format.parse(dates.get(1));
                Log.d("side2", "efe" + tempDate.toString()+ tempDate1.toString());
                if (tempDate.before(tempDate1)) {
                    birthday = dates.get(0);
                    expireDate = dates.get(1);
                } else {
                    birthday = dates.get(1);
                    expireDate = dates.get(0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }*/

    /*private  String getDate(String desc, String type) {
        int count=0;
        Matcher m = null;
        String allMatches = new String();
        //desc = "19.01.1998.";
        //Matcher m = Pattern.compile("\\d\\d.\\d\\d.\\d\\d\\d\\d.").matcher(desc);
        if(type.toLowerCase().equals("date")) m = Pattern.compile("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}").matcher(desc);
        if(type.toLowerCase().equals("documentnumber")){
            m = Pattern.compile("[0-9]{9}").matcher(desc);
        }
        if(type.toLowerCase().equals("oib")) m = Pattern.compile("[0-9]{11}").matcher(desc);
        if(type.toLowerCase().equals("name")) m = Pattern.compile("[A-Z]{3,}").matcher(desc);

        while (m.find()) {
            allMatches = m.group();
        }
        return allMatches;
    }

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
