package com.main;

import com.filetool.FileUtil;
import com.filetool.LogUtil;
import com.searchpath.Deploy;

/**
 * 
 * 工具入口
 * 
 * @version  [版本号, 2017-1-9]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class Main
{
    public static void main(String[] args){
   	
    	String currentPath=System.getProperty("user.dir");   //获得当前程序目录   
        String graphFilePath = currentPath+"/case.txt";
        String resultFilePath = currentPath+"/result.txt";
        LogUtil.printLog("Begin");
        // 读取输入文件
        String[] graphContent = FileUtil.read(graphFilePath, null);
        //功能实现入口
        String[] resultContents = Deploy.searchPath(graphContent);
       // 写入输出文件
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
