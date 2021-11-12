package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.Collator;
import java.util.*;

import net.sourceforge.pinyin4j.*;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


public class Controller {
    @FXML
    private Button myButton;
    @FXML
    private TextField filePath;
    @FXML
    private TextArea fileNames;
    @FXML
    private Text runInfo;
    /**
     * 通过集合中汉字排序
     */
    private void listSortByPinYin(List<File> fileList){
        final HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        // 输出设置，大小写，音标方式
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String name1 = null;
                String name2 = null;
                try {
                    name1 = PinyinHelper.toHanYuPinyinString((String)o1.getName(), defaultFormat, " ", true);
                    name2 = PinyinHelper.toHanYuPinyinString((String)o2.getName(), defaultFormat, " ", true);
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
                return name1.compareTo(name2);
            }
        });
    }
    public void editFilesName() throws IOException {
        if (fileNames.getText().trim().isEmpty()) {
            runInfo.setText("待修改的文件名有未填写");
            return;
        }
        if (filePath.getText().trim().isEmpty()){
            runInfo.setText("请填写文件夹路径！");
            return;
        }
        File folder = new File(filePath.getText().trim());
        if (folder.exists()){
            File[] fileArray = folder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isHidden() && pathname.isFile();
                }
            });
            if (null == fileArray || fileArray.length == 0) {
                runInfo.setText("文件夹为空文件夹！");
                return;
            }
            List<File> fileList = Arrays.asList(fileArray);
            this.listSortByPinYin(fileList);
            //Collections.sort(fileList ,(o1, o2) -> Collator.getInstance(Locale.CHINA).compare(o1.getName(), o2.getName()));
            //根据换行切分字符串
            String[] fileNamesArrary = fileNames.getText().trim().split("\\r?\\n");
            for (int i = 0; i < fileArray.length; i++){
                String parentPath = fileArray[i].getParent();
                String oldName = fileArray[i].getName();
                System.out.println(oldName);
                String extensionName = oldName.substring(oldName.lastIndexOf(".")).toLowerCase();
                //防止数组越界
                if (i >= fileNamesArrary.length) {
                    return;
                }
                System.out.println(fileNamesArrary[i]);
                String newPath = parentPath + "/" + fileNamesArrary[i] + extensionName;
                File newName = new File(newPath);
                fileArray[i].renameTo(newName);
            }

        }else {
            runInfo.setText("文件夹不存在，请检查路径是否准确！");
            return;
        }
        runInfo.setText("SUCCESS");

    }

}
