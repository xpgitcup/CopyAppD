/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copyappc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author LiXiaoping
 */
public class TargetApplication {

    
    ApplicationProperty sourceApp = new ApplicationProperty();
    ApplicationProperty targetApp = new ApplicationProperty();
    
    public TargetApplication() {
        targetApp.setAppPath(CopyAppC.getCurrentRunPath());
        targetApp.setAppName("NoNamed");
        targetApp.setSystemDB("NoNamed");
        targetApp.setUserDB("NoNamed");
    }

    @Override
    public String toString() {
        return "${appName}/(${systemDB},${userDB})@${appPath}";
    }

    /**
     * 功能：Java读取txt文件的内容 步骤：1：先获得文件句柄 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流 4：一行一行的输出。readline()。 备注：需要考虑的是异常情况
     *
     * @param filePath
     * @return
     */
    public List<String> readTextFileToList(String filePath) {
        List<String> strings = new ArrayList<>();
        try {
            String encoding = "UTF-8";// "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); //考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    strings.add(lineTxt);
                    //System.out.println(lineTxt);
                }
            } else {
                System.out.println("找不到指定的文件:" + filePath);
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错:" + e.getMessage());
        }
        return strings;
    }

    /**
     * 修改工程文件中的工程名称.project
     */
    public void updateProjectFile() {
        String pName = String.format("%s\\%s\\.project", sourceApp.getAppPath(), sourceApp.getAppName());
        System.out.printf("工程文件：%s\n", pName);
        File pFile = new File(pName);

        List<String> strings = readTextFileToList(pName);
        
        printList(strings);
        
        String source = String.format("<name>%s</name>", sourceApp.getAppName());
        String target = String.format("<name>%s</name>", targetApp.getAppName());
        
        System.out.printf("关键字：%s --> %s\n", source, target);

    }

    private void printList(List<String> strings) {
        strings.stream().forEach((e) -> {
            System.out.println(e);
        });
    }

    /**
     * 修改工程名称
     */
    public void updateProjectName() {
        String pName = String.format("%s\\%s\\application.properties", targetApp.getAppPath(), targetApp.getAppName());
        //def oName = "${appPath}\\${appName}\\application.propertiesA"
        System.out.printf("关键文件：%s\n", pName);
        File pFile = new File(pName);
        Properties properties = new Properties();
        try {
            if (pFile.exists()) {
                FileInputStream fis = new FileInputStream(pFile);
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                properties.load(isr);
                System.out.println(properties.getProperty("app.name"));
                //----------------------------------------------------------------------------------------------------------
                properties.setProperty("app.name", targetApp.getAppName());
                if (pFile.setWritable(true)) {
                    FileOutputStream fos = new FileOutputStream(pFile);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                    System.out.println(properties.getProperty("app.name"));
                    properties.store(osw, "Grails Metadata file");
                    System.out.printf("修改工程名称为：%s\n", targetApp.getAppName());
                }
            } else {
                System.out.printf("找不到关键文件：%s\n", pFile.getName());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 拷贝文件
     *
     * @param properties
     */
    public void copyApplication(Properties properties) {
        String targetApplication = String.format("%s\\%s", targetApp.getAppPath(), targetApp.getAppName());
        String source = properties.getProperty("主目录");
        String commond = String.format("xcopy %s %s /E /I /Y", source, targetApplication);
        System.out.println(commond);
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(commond);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * 处理命令行参数
     *
     * @param args
     */
    public void processArgs(String[] args) {
        Integer argsCount = args.length;

        switch (argsCount) {
            case 0:
                targetApp.setAppName("NoNamed");
                targetApp.setSystemDB("NoNamed");
                targetApp.setUserDB("NoNamed");
                break;
            case 1:
                //只有一个参数
                targetApp.setAppName(args[0]);
                targetApp.setSystemDB(targetApp.getAppName() + "_systemDB");
                targetApp.setUserDB(targetApp.getAppName() + "_userDB");
                break;
            case 2:
                //有两个参数
                targetApp.setAppName(args[0]);
                targetApp.setSystemDB(args[1]);
                targetApp.setUserDB(targetApp.getAppName() + "_userDB");
                break;
            case 3:
                //有三个参数
                targetApp.setAppName(args[0]);
                targetApp.setSystemDB(args[1]);
                targetApp.setUserDB(args[2]);
                break;
            default:
                targetApp.setAppName(args[0]);
                targetApp.setSystemDB(args[1]);
                targetApp.setUserDB(args[2]);
                break;
        }
    }

}
