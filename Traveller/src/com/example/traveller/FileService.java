package com.example.traveller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.R.integer;
import android.content.Context;
import android.util.Log;

public class FileService {
	private Context context;
	public int count;//保存有多少个地理位置坐标
		
	public FileService(Context context)
	{
		this.context=context;
	}
	
	//保存位置信息
	public void savePosition(File targetFile, String content) throws Exception
	{
//		FileOutputStream outputStream=new FileOutputStream(targetFile);
//		outputStream.write(content.getBytes());
//		outputStream.close();
		BufferedWriter out = null;  
        try {  
            out = new BufferedWriter(new OutputStreamWriter(  
                    new FileOutputStream(targetFile, true)));  
            out.write(content); 
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                out.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
	}
	
	//按行读取内容
	public float[][] readFileOnLine(String strFileName) throws Exception
	{
		float[][] result=new float[200][2];
		count=0;
		FileInputStream fis =new FileInputStream(strFileName);  
		DataInputStream dataIO = new DataInputStream(fis);  
		String strLine = null; 
		while((strLine =  dataIO.readLine()) != null) {  
//		    sBuffer.append(strLine + "\n");
			if(strLine.indexOf("/")>0)
			{
				int breakPoint=strLine.indexOf("/");
				result[count][0]=Float.parseFloat(strLine.substring(0, breakPoint));
				result[count][1]=Float.parseFloat(strLine.substring(breakPoint+1, strLine.length()));
				count++;
			}
		}  
		dataIO.close();  
		fis.close(); 
		return result;
	}
}
