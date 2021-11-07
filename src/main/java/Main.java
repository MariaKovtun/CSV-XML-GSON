import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        //парсинг csv
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String jsonFromCSV = listToJson(list);
        writeString(jsonFromCSV);

        //парсинг xml
        List<Employee> xmlList = parseXML("data.xml");
        String jsonFromXML = listToJson(xmlList);
        writeString(jsonFromXML);

        //парсинг json
        List<String> jsonInit = getJsonContent("res.json");
        List<Employee> listFromJson = jsonToList(jsonInit);
        listFromJson.forEach(elem -> System.out.println(elem.toString()));
    }

    private static List<Employee> jsonToList(List<String> jsonElems) {
        List<Employee> res = new ArrayList<Employee>();

        for (String jsonElem: jsonElems) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            res.add(gson.fromJson(jsonElem,Employee.class));

        }
        return res;
    }

    private static List<String> getJsonContent(String jsonFileName) throws FileNotFoundException {
        List<String> res = new ArrayList<>();

        try {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArr = (JSONArray) jsonParser.parse(new FileReader(jsonFileName));
            jsonArr.forEach(jsonElem ->
                    res.add(jsonElem.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
      return res;
    }

    private static List<Employee> parseXML(String xmlFile) {
        List<Employee> res = new ArrayList<Employee>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(xmlFile));

            NodeList employees = doc.getElementsByTagName("employee");

            for (int i=0;i<employees.getLength();i++){
                NamedNodeMap content = employees.item(i).getAttributes();

                int id = Integer.parseInt(content.getNamedItem("id").getNodeValue());
                String firstName = content.getNamedItem("firstName").getNodeValue();
                String secondName = content.getNamedItem("lastName").getNodeValue();
                String country = content.getNamedItem("country").getNodeValue();
                int age = Integer.parseInt(content.getNamedItem("age").getNodeValue());

                res.add(new Employee(id,firstName,secondName,country,age));
               }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private static void writeString(String json) {
        try (FileWriter writer = new FileWriter("res.json", true)) {
            writer.write(json);
            writer.flush();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();

        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String listToJson(List<Employee> staff) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(staff,listType);
    }
}
