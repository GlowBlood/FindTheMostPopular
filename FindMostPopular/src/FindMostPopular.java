import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;


public class FindMostPopular {

    //Файл с исходными данными, 3 поля: datatime, текст запроса, true/false (успешность)
    private static final String FILE_NAME = "C:/Users/User/Downloads/example1.xls";

    public static void main(String args[]) throws ParseException {
        //Задаем временной промежуток
        String time_range = "year";
        //Вызываем методы
        String most_popular = get_moda(data_filter(normalize(get_from_excel()), time_range));
        //Печатаем результат
        System.out.println(most_popular);

    }

    // Метод из excel превращает в ArrayList
    public static ArrayList<String> get_from_excel() throws ParseException {

        ArrayList<String> my_list = new ArrayList<String>();

        try {

            FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
            Workbook workbook = new HSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator iterator = datatypeSheet.iterator();


            while (iterator.hasNext()) {

                Row currentRow = (Row) iterator.next();
                Iterator cellIterator = currentRow.iterator();

                while (cellIterator.hasNext()) {

                    Cell currentCell = (Cell) cellIterator.next();
                    if (currentCell.getCellType() == CellType.STRING) {
                        my_list.add(currentCell.getStringCellValue());
                        //System.out.print(currentCell.getStringCellValue() + "--");
                    } else if (currentCell.getCellType() == CellType.NUMERIC) {
                        String s1 = currentCell.getNumericCellValue() + "";
                        my_list.add(s1);
                        //System.out.print(currentCell.getNumericCellValue() + "--");
                    }

                }
                //Выводим все строки из excel
                //System.out.println();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return my_list;
        //normalize(my_list);
    }

    // Метод нормализует ArrayList: удаляет true, false
    public static ArrayList<String> normalize(ArrayList<String> parent_list) throws ParseException {

        for (int i = 0; i < parent_list.size();) {

            // Строки, которые false - удаляем целиком, строки, которые true - удаляем слово true
            if (Objects.equals(parent_list.get(i), "false")) {
                parent_list.remove(i);
                parent_list.remove(i - 1);
                parent_list.remove(i - 2);
                i = i + 1;
            }
            i = i + 1;
        }

        for (int i = 0; i < parent_list.size();){

            if (Objects.equals(parent_list.get(i), "true")) {
                parent_list.remove(i);

            }
            i = i+1;
        }

        return parent_list;
    }

    // Метод отфильтровывает запросы за временной промежуток
    public static ArrayList<String> data_filter(ArrayList<String> parent_list, String time_range) {

        ArrayList<String> actualList = new ArrayList<String>();

        Calendar date_of_action = new GregorianCalendar();
        Calendar today = new GregorianCalendar();

        if (time_range == "year") today.add(Calendar.YEAR, -1);
        else if (time_range == "month") today.add(Calendar.MONTH, -1);
        else if (time_range == "week") today.add(Calendar.WEEK_OF_MONTH, -1);
        else if (time_range == "day") today.add(Calendar.HOUR_OF_DAY, -24);

        for (int i =0; i < parent_list.size();){
            String strDate = parent_list.get(i);

            //парсим из строки в календарь
            date_of_action.set(Calendar.YEAR, Integer.parseInt(strDate.substring(0, 4)));
            date_of_action.set(Calendar.MONTH, Integer.parseInt(strDate.substring(5, 7)));
            date_of_action.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strDate.substring(8, 10)));
            date_of_action.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strDate.substring(11, 13)));

            //в новый массив отправляем запросы, которые устраивают нас по дате
            if (date_of_action.after(today))
            {
                actualList.add(parent_list.get(i+1));
            }
            i = i + 2;
        }
        return actualList;
    }

    private static Map <String, Integer> mElementsMap;

    // Метод получает моду от массива: наиболее часто встречающийся элемент
    public static String get_moda (ArrayList<String> actualList){
        mElementsMap = new HashMap <> ();

        for (int i =0; i < actualList.size(); i++)
        {
            String element = actualList.get(i);
            if (mElementsMap.containsKey(element)) {
                mElementsMap.put(element, mElementsMap.get(element) + 1);
                continue;
            }
            mElementsMap.put(element, 1);

        }

        //System.out.println(mElementsMap);

        int maximalCount = 0;
        String resultElement = null;
        for (Map.Entry <String, Integer> currentEntry : mElementsMap.entrySet()) {
            if (currentEntry.getValue() > maximalCount) {
                maximalCount = currentEntry.getValue();
                resultElement = currentEntry.getKey();
            }
          }
        //System.out.println(resultElement);
        //System.out.println("Элемент: " + resultElement + " (" + maximalCount + " раз)");
        return(resultElement);

    }
}

