import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String CsvFileName = "data.csv";
        String XMLFileName = "data.xml";
        String JsonFileName = "data.json";
        String JsonFileName2 = "data2.json";


        List<Employee> employeeList = parseCSV(columnMapping, CsvFileName);

        for (Employee employee : employeeList) {
            System.out.println(employee);
        }

        System.out.println(listToJson(employeeList));

        writeString(listToJson(employeeList), JsonFileName);

        System.out.println("XMLParse:");

        List<Employee> employeeListXML = parseXML(XMLFileName);
        writeString(listToJson(employeeListXML), JsonFileName2);

    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strat = new ColumnPositionMappingStrategy<>();
            strat.setType(Employee.class);
            strat.setColumnMapping(columnMapping);

            CsvToBean csv = new CsvToBeanBuilder(reader)
                    .withMappingStrategy(strat)
                    .build();

            list = csv.parse();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employeeList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fileName);

            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            long id = 0L;
            String firstName = "";
            String lastName = "";
            String country = "";
            int age = 0;
            for (int i = 0; i < nodeList.getLength(); i++) {

                if (!nodeList.item(i).getNodeName().equals("employee")) {
                    continue;
                }

                NodeList nodeListEmployee = nodeList.item(i).getChildNodes();

                for (int j = 0; j < nodeListEmployee.getLength(); j++) {

                    if (nodeListEmployee.item(j).getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    switch (nodeListEmployee.item(j).getNodeName()) {
                        case "id":
                            id = Long.parseLong(nodeListEmployee.item(j).getTextContent());
                            break;
                        case "firstName":
                            firstName = nodeListEmployee.item(j).getTextContent();
                            break;
                        case "lastName":
                            lastName = nodeListEmployee.item(j).getTextContent();
                            break;
                        case "country":
                            country = nodeListEmployee.item(j).getTextContent();
                            break;
                        case "age":
                            age = Integer.parseInt(nodeListEmployee.item(j).getTextContent());
                            break;
                    }
                }
                employeeList.add(new Employee(id, firstName, lastName, country, age));
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return employeeList;
    }

    public static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(list);
        return json;
    }

    public static void writeString(String output, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(output);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
