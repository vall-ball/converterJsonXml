package converter;

import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {


//String str = "<element><deep deepattr=\"deepvalue\"><element>1</element><element>2</element><element>3</element></deep></element>";

             FileHandler fileHandler = new FileHandler("test.txt");
        String str = fileHandler.load();
        fileHandler.save(str);


        Converter converter = new Converter();
        if (str.substring(0, 1).equals("<")) {
            XMLParser xmlParser = new XMLParser();
            String preparedXml = xmlParser.prepareXML(str);
         fileHandler.save(preparedXml);
            Element rootElement = xmlParser.createRootElement(preparedXml);

            List<Element> list = new ArrayList<>();
            xmlParser.getAllElements(list, rootElement);
            JsonObject jsonObject = converter.convertFromXmlToJson(list.get(0));
            System.out.println("{" + jsonObject.format() + "}");
        } else {
            JsonParserNew jsonParser = new JsonParserNew();
            String preparedJson = jsonParser.prepareJSON(str);
          fileHandler.save(preparedJson);
            List<JsonObject> list = jsonParser.listChildren(preparedJson, null);
            List<JsonObject> listOfAll = new ArrayList<>();
            for (JsonObject j : list) {
                jsonParser.generateAllJsonObject(listOfAll, j);
            }
            List<JsonObject> roots = rootElements(listOfAll);
            if (roots.size() == 1) {
                Element element = converter.convertFromJsonToXml(roots.get(0));
                System.out.println(element.format());
            } else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.name = "root";
                jsonObject.children = roots;
                for (JsonObject j : jsonObject.children) {
                    j.parent = jsonObject;
                }
                Element element = converter.convertFromJsonToXml(jsonObject);
                System.out.println(element.format());
            }
        }
    }

    public static List<JsonObject> rootElements(List<JsonObject> list) {
        List<JsonObject> answer = new ArrayList<>();
        for (JsonObject o : list) {
            if (o.parent == null) {
                answer.add(o);
            }
        }
        return answer;
    }
}



///////////////////////////////////////////////////////////////////////
        /*
        String str = "{\n" +
                "    \"transaction\": {\n" +
                "        \"id\": \"6753322\",\n" +
                "        \"number\": {\n" +
                "            \"@region\": \"Russia\",\n" +
                "            \"#number\": \"8-900-000-000\"\n" +
                "        },\n" +
                "        \"empty1\": null,\n" +
                "        \"empty2\": { },\n" +
                "        \"empty3\": \"\",\n" +
                "        \"inner1\": {\n" +
                "            \"inner2\": {\n" +
                "                \"inner3\": {\n" +
                "                    \"key1\": \"value1\",\n" +
                "                    \"key2\": \"value2\"\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"inner4\": {\n" +
                "            \"@\": 123,\n" +
                "            \"#inner4\": \"value3\"\n" +
                "        },\n" +
                "        \"inner5\": {\n" +
                "            \"@attr1\": 123.456,\n" +
                "            \"#inner4\": \"value4\"\n" +
                "        },\n" +
                "        \"inner6\": {\n" +
                "            \"@attr2\": 789.321,\n" +
                "            \"#inner6\": \"value5\"\n" +
                "        },\n" +
                "        \"inner7\": {\n" +
                "            \"#inner7\": \"value6\"\n" +
                "        },\n" +
                "        \"inner8\": {\n" +
                "            \"@attr3\": \"value7\"\n" +
                "        },\n" +
                "        \"inner9\": {\n" +
                "            \"@attr4\": \"value8\",\n" +
                "            \"#inner9\": \"value9\",\n" +
                "            \"something\": \"value10\"\n" +
                "        },\n" +
                "        \"inner10\": {\n" +
                "            \"@attr5\": null,\n" +
                "            \"#inner10\": null\n" +
                "        },\n" +
                "        \"inner11\": {\n" +
                "            \"@\": null,\n" +
                "            \"#\": null\n" +
                "        },\n" +
                "        \"inner12\": {\n" +
                "            \"@somekey\": \"attrvalue\",\n" +
                "            \"#inner12\": null,\n" +
                "            \"somekey\": \"keyvalue\",\n" +
                "            \"inner12\": \"notnull\"\n" +
                "        },\n" +
                "        \"\": {\n" +
                "            \"#\": null,\n" +
                "            \"secret\": \"this won't be converted\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"meta\": {\n" +
                "        \"version\": 0.01\n" +
                "    }\n" +
                "}";

        JSONParser jsonParser = new JSONParser();
        String preparedJson = jsonParser.prepareJSON(str);
        List<JsonObject> list = jsonParser.listChildren(preparedJson, null);


        List<JsonObject> listOfAll = new ArrayList<>();
        for (JsonObject j : list) {
            jsonParser.generateAllJsonObject(listOfAll, j);
        }


        for (JsonObject o : listOfAll) {
           System.out.println(o.info() + o.children);
        }

        System.out.println("FORMAT JSON");
        System.out.println(listOfAll.get(0).format());
        */
/////////////////////////////////////////////////////
        /*
        String xml = "<root>\n" +
                "    <id>6753322</id>\n" +
                "    <number region=\"Russia\">8-900-000-00-00</number>\n" +
                "    <nonattr1 />\n" +
                "    <nonattr2></nonattr2>\n" +
                "    <nonattr3>text</nonattr3>\n" +
                "    <attr1 id=\"1\" />\n" +
                "    <attr2 id=\"2\"></attr2>\n" +
                "    <attr3 id=\"3\">text</attr3>\n" +
                "    <email>\n" +
                "        <to>to_example@gmail.com</to>\n" +
                "        <from>from_example@gmail.com</from>\n" +
                "        <subject>Project discussion</subject>\n" +
                "        <body font=\"Verdana\">Body message</body>\n" +
                "        <date day=\"12\" month=\"12\" year=\"2018\"/>\n" +
                "    </email>\n" +
                "</root>";
        XMLParser xmlParser = new XMLParser();
        String preparedXml = xmlParser.prepareXML(xml);
        Element rootElement = xmlParser.createRootElement(preparedXml);

        List<Element> list = new ArrayList<>();
        xmlParser.getAllElements(list, rootElement);
        for (Element e : list) {
            System.out.println(e.info());
            System.out.println();
        }

        System.out.println(list.get(0).format());

        Converter converter = new Converter();
        JsonObject jsonObject = converter.convertFromXmlToJson(list.get(0));
        System.out.println(jsonObject.format());*/