package chat_client;

import java.io.*;
import java.util.ArrayList;


public class WriteReadInformation {
    private static String catalog="C:\\Users\\Cats\\Documents\\Учеба\\java\\lesson3\\history";

    public static ArrayList<String> Cenzor() throws IOException {
        String filename="C:\\Users\\Cats\\Documents\\Учеба\\java\\lesson3\\Cenzor.txt";
        ArrayList<String> result=new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String x;
        while ((x=bufferedReader.readLine())!= null) {
            result.add(x);
       }
        return result;

    }

    public static void WriteInformation(String user, String msg) throws IOException {

        FileOutputStream fileOutputStream=new FileOutputStream(catalog+".txt",true);
      msg="";
        for (int i = 0; i <150 ; i++) {
          msg+=i+' '+ user + " "+"text \n" ;
        }
        fileOutputStream.write(msg.getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();

    }

    public static String ReadInformation(String user) throws IOException {
        String filename=catalog+".txt";
        File file=new File(filename);
        if (!file.isFile()){
            file.createNewFile();
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            String x;
            String result="";
            int dx=0;

            while ((x=bufferedReader.readLine())!= null && dx<100) {
                result+= x +"\n";
                dx+=1;
            }
         return result;




    }
}
