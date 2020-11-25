package converter;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    public JsonObject convertFromXmlToJson(Element element) {
        //System.out.println(element.children);
        JsonObject jsonObject = new JsonObject();
        jsonObject.name = element.tagName;
        if (element.attributes != null && element.attributes.length != 0) {
            for (String s : element.attributes) {
                jsonObject.attributes.add(s);
            }
            List<JsonObject> attr = attributesFromXml(element);
            jsonObject.children.addAll(attr);
            if (element.value != null && element.value.equals("*")) {
                JsonObject val = new JsonObject();
                val.name = "#" + element.tagName;
                jsonObject.children.add(val);
                if (element.children != null && element.children.size() > 0) {
                    for (Element el : element.children) {
                        val.children.add(convertFromXmlToJson(el));
                    }
                }
            } else {
                JsonObject val = new JsonObject();
                val.name = "#" + element.tagName;
                val.value = element.value;
                jsonObject.children.add(val);
                if (element.children != null && element.children.size() > 0) {
                    for (Element el : element.children) {
                        jsonObject.children.add(convertFromXmlToJson(el));
                    }
                }

            }
        } else {
            if (element.children != null && element.children.size() > 0) {
                for (Element el : element.children) {
                    jsonObject.children.add(convertFromXmlToJson(el));
                }
            }
            jsonObject.value = element.value;
        }

        return jsonObject;
    }

    public List<JsonObject> attributesFromXml(Element element) {
        List<JsonObject> attributes = new ArrayList<>();
        for (String s : element.attributes) {
            String[] m = s.split("=");
            JsonObject o = new JsonObject();
            o.name = "@" + m[0].trim();
            o.value = m[1];
            attributes.add(o);
        }
        return attributes;
    }

    public Element convertFromJsonToXml(JsonObject jsonObject) {
        Element element = new Element();
        element.tagName = jsonObject.name;
        element.value = jsonObject.value;
        element.content = jsonObject.content;

        if (jsonObject.children != null && jsonObject.children.size() != 0) {
            if (hasAttributeChildren(jsonObject)) {
                jsonObject.attributes = null;
                for (JsonObject o : jsonObject.children) {
                    if (o.name.substring(0, 1).equals("@") || o.name.substring(0, 1).equals("#")) {
                        o.name = o.name.substring(1);
                    }
                }
            }
            for (JsonObject o : jsonObject.children) {
                if (!o.name.substring(0, 1).equals("@") && !o.name.substring(0, 1).equals("#")) {
                    element.children.add(convertFromJsonToXml(o));
                }
            }
        }
        if (jsonObject.attributes != null && jsonObject.attributes.size() != 0) {
            String[] attr = new String[jsonObject.attributes.size()];
            for (int i = 0; i < attr.length; i++) {
                attr[i] = jsonObject.attributes.get(i);
            }
            element.attributes = attr;
        }
        return element;
    }

    public boolean hasAttributeChildren(JsonObject parent) {
        for (JsonObject o : parent.children) {
            if (o.name.substring(0, 1).equals("@") && o.children != null && o.children.size() != 0) {
                return true;
            }
        }
        return false;
    }





}
 /* public void ifAttributeHasChildren(JsonObject parent, Element element) {
        parent.attributes = null;
        List<JsonObject> list = new ArrayList<>();
        for (JsonObject o : parent.children) {
            if (o.name.substring(0, 1).equals("@") || o.name.substring(0, 1).equals("#")) {
                o.name = o.name.substring(1);
                list.add(o);
            }
        }
        for (JsonObject o : list) {
            element.children.add(convertFromJsonToXml(o));
        }
    }*/