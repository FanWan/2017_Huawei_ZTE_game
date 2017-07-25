package com.main;

import com.filetool.FileUtil;
import com.filetool.LogUtil;
import com.searchpath.Deploy;

/**
 * 
 * �������
 * 
 * @version  [�汾��, 2017-1-9]
 * @see  [�����/����]
 * @since  [��Ʒ/ģ��汾]
 */
public class Main
{
    public static void main(String[] args){
   	
    	String currentPath=System.getProperty("user.dir");   //��õ�ǰ����Ŀ¼   
        String graphFilePath = currentPath+"/case.txt";
        String resultFilePath = currentPath+"/result.txt";
        LogUtil.printLog("Begin");
        // ��ȡ�����ļ�
        String[] graphContent = FileUtil.read(graphFilePath, null);
        //����ʵ�����
        String[] resultContents = Deploy.searchPath(graphContent);
       // д������ļ�
        if (hasResults(resultContents))
            FileUtil.write(resultFilePath, resultContents, false);
        else
            FileUtil.write(resultFilePath, new String[] { "NA" }, false);
        LogUtil.printLog("End");
    }
    
    private static boolean hasResults(String[] resultContents)
    {
        if(resultContents==null)
        {
            return false;
        }
        for (String contents : resultContents)
        {
            if (contents != null && !contents.trim().isEmpty())
            {
                return true;
            }
        }
        return false;
    }

}
