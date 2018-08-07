package com.cubosystems.findme.Models;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

/**
 * Created by Administrator on 7/23/2018.
 */

public class Helper {
    public final static String USER_INFO_FILE_NAME = "user_info.txt";
    public final static String USER_LANG_FILE_NAME = "lang.txt";

    /***
     *
     * @return a non negative integer ranging between 100000 and 999999
     */
    static String generateInt(){
        Random r = new Random();
        int lowerBound = 100000, upperBound = 900000, randomNum = r.nextInt(upperBound);

        //verifying if the generated number is a non negative number
        randomNum = randomNum < 0 ? randomNum * -1 : randomNum;
        return String.valueOf(lowerBound + randomNum );
    }

    /**
     *
     * @param idType indicate the type of ID that needs to be generated. either "guard" or "person"
     * @return a unique string identifier
     */
    public static String generateID(String idType){
        String identifier = "";
        if(idType.toLowerCase().equals("guard")){
            identifier = "g" + generateInt();
        }else if(idType.toLowerCase().equals("person")){
            identifier = "p" + generateInt();
        }
        return identifier;
    }

    /**
     * this method will store the guard ID in a file
     * @param userIdentifier the ID of the guard that needs to be saved
     * @param context the context
     * @return returns true if the operations is successful, false otherwise
     */
    public static boolean storeIDInFile(String userIdentifier, Context context){
        try{
            String ff = context.getFilesDir().getAbsolutePath() + "/" + Helper.USER_INFO_FILE_NAME;
            File file = new File(ff);

            FileWriter fileWriter = new FileWriter(file,false);
            fileWriter.write(userIdentifier);
            fileWriter.flush();
            fileWriter.close();
            return true;
        }catch(Exception ex){
            Log.e("FUNCTION","storeDetailsInFile()");
            ex.printStackTrace();
            return false;
        }
    }

    public static String getGuardID(Context context){
        String guardID = "";
        try{
            String ff = context.getFilesDir().getAbsolutePath() + "/" + Helper.USER_INFO_FILE_NAME;
            File file = new File(ff);
            if(!file.exists()){
                return "";
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while((line = br.readLine()) != null){
                guardID = line.trim();
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return guardID;
    }

    public static String getAppLang(Context context){
        String language = "";
        try{
            String ff = context.getFilesDir().getAbsolutePath() + "/" + Helper.USER_LANG_FILE_NAME;
            File file = new File(ff);

            if(!file.exists()){
                language = "";
            }else{
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String s;

                while((s = bufferedReader.readLine()) != null){
                    language = s.trim();
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            language = "";
        }

        return language;
    }


    public static boolean setAppLang(Context context, String language){
        boolean result = false;
        try{
            String ff = context.getFilesDir().getAbsolutePath() + "/" + Helper.USER_LANG_FILE_NAME;
            File file = new File(ff);

            FileWriter fileWriter = new FileWriter(file,false);
            fileWriter.write(language);
            fileWriter.flush();
            fileWriter.close();
            result = true;
        }catch(Exception ex){
            ex.printStackTrace();
            result = false;
        }

        return result;
    }

}
