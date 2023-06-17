package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        // CSV to JSON
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameJ1 = "data.json";
        String fileNameJ2 = "data2.json";
        String fileNameX = "data.xml";
        List<Employee> listCsv = parseCSV(columnMapping, fileName);
        String json = listToJson(listCsv);
        writeString(json, fileNameJ1);
        // XML to JSON
        List<Employee> listXml = parseXML(fileNameX);
        String json2 = listToJson(listXml);
        writeString(json2, fileNameJ2);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>(){}.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Employee> parseXML(String fileName) throws IOException, SAXException, ParserConfigurationException {
        List <Employee> staff = new ArrayList<>();
        try {
            File inputFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Корневой элемент: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("employee");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                System.out.println("\nCurrent Element: " + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    System.out.println("Employee id: " + element.getElementsByTagName("id").item(0).getTextContent());
                    System.out.println("First Name: " + element.getElementsByTagName("firstName").item(0).getTextContent());
                    System.out.println("Last Name: " + element.getElementsByTagName("lastName").item(0).getTextContent());
                    System.out.println("Country: " + element.getElementsByTagName("country").item(0).getTextContent());
                    System.out.println("Age: " + element.getElementsByTagName("age").item(0).getTextContent());
                    String id = element.getElementsByTagName("id").item(0).getTextContent();
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    String age = element.getElementsByTagName("age").item(0).getTextContent();
                    Employee employee = new Employee(Long.parseLong(id), firstName, lastName, country,Integer.parseInt(age));
                    staff.add(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staff;
    }
}