/**
 * CopyAppA  程序名称 系统数据库名称 用户数据库名称
 * 当前目录就是目标程序所在目录
 * 系统数据库：程序名称_systemdb
 * 用户数据库：程序名称_userdb
 */
package copyappc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author LiXiaoping
 */
public class CopyAppC {

    //变量定义
    private String seedApplicationPath;
    private Properties seedProperties = new java.util.Properties();

    /**
     * 根据种子程序，拷贝生成目标程序 种子程序的原位置依据ini文件配置 参数： 第一：目标程序的名字
     * 第二：目标数据库的名称，考虑有两个以上数据库的情况 拷贝文件 修改程序名 修改数据库名称
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //处理命令行
        if (args.length < 1) {
            System.out.println("程序使用方法：");
            System.out.println("CopyAppA  程序名称 系统数据库名称 用户数据库名称");
            System.out.println("当前目录就是目标程序所在目录");
            System.out.println("系统数据库：程序名称_systemdb");
            System.out.println("用户数据库：程序名称_userdb");
        } else {
            System.out.println("当前命令行：");
            for (String e : args) {
                System.out.println(e);
            }
        }

        System.out.printf("-------------------------------------------------------------------\n");
        
        //处理配置文件
        CopyAppC mainObject = new CopyAppC();
        String currentPath = mainObject.getCurrentPath();
        //读取配置文件，获取种子程序的位置
        File iniFile = new File(String.format("%s/SeedApplication.ini", currentPath));
        if (iniFile.exists()) {
            try (FileInputStream fis = new FileInputStream(iniFile)) {
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                mainObject.getSeedProperties().load(isr);
                mainObject.getSeedProperties().stringPropertyNames().stream().forEach((_item) -> {
                    System.out.printf("%s:%s\n", _item, mainObject.seedProperties.get(_item));
                });
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CopyAppC.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(CopyAppC.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CopyAppC.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.printf("当前目录：%s\n", currentPath);
        } else {
            System.out.println("SeedApplication.ini 文件不存在！");
        }

        System.out.printf("-------------------------------------------------------------------\n");
        
        TargetApplication targetApplication = new TargetApplication();
        targetApplication.processArgs(args);
        targetApplication.copyApplication(mainObject.getSeedProperties());
        targetApplication.updateProjectName();
        targetApplication.updateProjectFile();
        
    }

    /**
     * 获取当前目录
     *
     * @return
     */
    public String getCurrentPath() {
        String tempa = this.getClass().getResource("CopyAppC.class").getPath();
        System.out.println(tempa);
        //判断执行环境
        if (tempa.contains("file:")) {
            tempa = tempa.substring(6);
            System.out.println(tempa);
            int k = tempa.indexOf("CopyAppC.jar");
            tempa = tempa.substring(0, k + "CopyAppC.jar".length());
        } else {
            tempa = tempa.substring(1);
        }
        File f = new File(tempa);
        tempa = f.getParent();
        System.out.println("当前类加载目录是：");
        System.out.println(tempa);
        return tempa;
    }

    static String getCurrentRunPath() {
        try {
            File f = new File(".");
            return f.getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(CopyAppC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * @return the seedApplicationPath
     */
    public String getSeedApplicationPath() {
        return seedApplicationPath;
    }

    /**
     * @param seedApplicationPath the seedApplicationPath to set
     */
    public void setSeedApplicationPath(String seedApplicationPath) {
        this.seedApplicationPath = seedApplicationPath;
    }

    /**
     * @return the seedProperties
     */
    public Properties getSeedProperties() {
        return seedProperties;
    }

    /**
     * @param seedProperties the seedProperties to set
     */
    public void setSeedProperties(Properties seedProperties) {
        this.seedProperties = seedProperties;
    }

}
