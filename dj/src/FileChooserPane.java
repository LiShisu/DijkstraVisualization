import javax.swing.*;

public class FileChooserPane extends JOptionPane {
    String[] options = {"示例1.txt","示例2.txt","示例3.txt"};

    public String getFileName(){
        int choice = JOptionPane.showOptionDialog(
                null,
                "请选择一个文件",
                "选择下载文件",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == -1){
            return "null";
        }else {
            return options[choice];
        }
    }
//    public void setOptions(String option){
//        options.
//    }
}
