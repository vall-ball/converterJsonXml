package converter;

import java.util.ArrayList;
import java.util.List;

public class JsonParserNew {

    public String prepareJSON(String json) {

        String firsr =  json.replaceAll("\\s*:\\s*", ":").
                replaceAll("\\n*", "").
                replaceAll("\\s*\\{\\s*", "{").
                replaceAll("\\s*\\}\\s*", "}").
                replaceAll("\\s*,\\s*",",").
                replaceAll("\\s*\\[\\s*", "[").
                replaceAll("\\s*\\]\\s*", "]");
        return firsr.replaceAll("\"@\":[0-9a-zA-Z]*,\"#", "\"").
                replaceAll("\"\":[0-9a-zA-Z]*,\"#", "\"");
    }

    public String getName(String json, int offset) {
        String firstPrepared = json.substring(offset);
        return firstPrepared.substring(firstPrepared.indexOf("\"") + 1, firstPrepared.indexOf("\":"));
    }

    public String getContent(String json, int offset) {
        String firstPrepared = json.substring(offset);
        String secondPrepared = firstPrepared.substring(firstPrepared.indexOf(":"));
        if (secondPrepared.substring(1, 2).equals("{") && secondPrepared.substring(2, 3).equals("}")) {
            return "\"\"";
        }
        if (secondPrepared.substring(1, 2).equals("[") && secondPrepared.substring(2, 3).equals("]")) {
            return "\"\"";
        }
        if (secondPrepared.substring(1, 2).equals("{")) {
            return secondPrepared.substring(1, indexOfClosingBrace(secondPrepared) + 1);
        } else if (secondPrepared.substring(1, 2).equals("[")) {
            return secondPrepared.substring(1, indexOfClosingSquareBrace(secondPrepared) + 1);
        } else {
            return secondPrepared.substring(1, indexOfEnd(secondPrepared));
        }
    }

    public String getContentInArray(String json, int offset) {
        String secondPrepared = json.substring(offset);
       // System.out.println(json);
        //System.out.println("offset = " + offset);
        //String secondPrepared = firstPrepared.substring(firstPrepared.indexOf(":"));
        if (secondPrepared.substring(1, 2).equals("{") && secondPrepared.substring(2, 3).equals("}")) {
            return "\"\"";
        }
        if (secondPrepared.substring(1, 2).equals("[") && secondPrepared.substring(2, 3).equals("]")) {
            return "\"\"";
        }
        if (secondPrepared.substring(1, 2).equals("{")) {
            return secondPrepared.substring(1, indexOfClosingBrace(secondPrepared) + 1);
        } else if (secondPrepared.substring(1, 2).equals("[")) {
            return secondPrepared.substring(1, indexOfClosingSquareBrace(secondPrepared) + 1);
        } else {
            return secondPrepared.substring(1, indexOfEnd(secondPrepared));
        }
    }

    public int indexOfClosingBrace(String str) {
        int open = 0;
        int close = 0;
        char[] strToChar = str.toCharArray();
        int index = 0;
        for (int i = 1; i < strToChar.length; i++) {
            if (strToChar[i] == '{') {
                open++;
            } else if (strToChar[i] == '}') {
                close++;
            }
            if (open == close) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int indexOfClosingSquareBrace(String str) {
        int open = 0;
        int close = 0;
        char[] strToChar = str.toCharArray();
        int index = 0;
        for (int i = 1; i < strToChar.length; i++) {
            if (strToChar[i] == '[') {
                open++;
            } else if (strToChar[i] == ']') {
                close++;
            }
            if (open == close) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int indexOfEnd(String str) {
        int index = 0;
        char[] strToChar = str.toCharArray();
        for (int i = 1; i < strToChar.length; i++) {
            if (strToChar[i] == ',' || strToChar[i] == '}' || strToChar[i] == ']') {
                index = i;
                break;
            }
        }
        return index;
    }

    public List<JsonObject> listChildren(String content, JsonObject parent) {
        int offset = 0;
        List<JsonObject> answer = new ArrayList<>();
        int braces = howManyBracesInTheEnd(content);
        while (offset < content.length() - braces) {
            if (!isContentArray(content)) {
                String childName = getName(content, offset);
                offset += childName.length() + 3;
                String childContent = getContent(content, offset);
                offset += childContent.length() + 1;
                if (childName.length() != 0) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.name = childName;
                    jsonObject.content = childContent;
                    jsonObject.parent = parent;
                    if (hasChildren(jsonObject)) {
                        jsonObject.children = listChildren(childContent, jsonObject);
                        jsonObject.value = generateValue(jsonObject, listChildren(childContent, jsonObject));
                        jsonObject.attributes = generateAttributes(jsonObject, listChildren(childContent, jsonObject));
                    } else {
                        jsonObject.value = childContent;
                    }
                    answer.add(jsonObject);
                    if (parent != null && hasListInValue(answer, parent)) {
                        JsonObject o = valueAsList(answer, parent);
                        for (JsonObject j : o.children) {
                            j.parent = parent;
                            answer.add(j);
                        }
                        answer.remove(o);
                    }
                }
                if (parent != null && !parent.hasValueAsList) {
                    List<JsonObject> correct = new ArrayList<>();
                    for (JsonObject j : answer) {
                        if (containsAnalog(j.name, answer)) {
                            correct.add(j);
                        }
                    }
                    for (JsonObject j : correct) {
                        if (answer.contains(j)) {
                            answer.remove(j);
                        }
                    }
                }
            } else {
                String childName = "element";
                //offset++;
                String childContent = getContentInArray(content, offset);
                offset += childContent.length() + 1;
                if (childName.length() != 0) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.name = childName;
                    jsonObject.content = childContent;
                    jsonObject.parent = parent;
                    if (hasChildren(jsonObject)) {
                        jsonObject.children = listChildren(childContent, jsonObject);
                        jsonObject.value = generateValue(jsonObject, listChildren(childContent, jsonObject));
                        jsonObject.attributes = generateAttributes(jsonObject, listChildren(childContent, jsonObject));
                    } else {
                        jsonObject.value = childContent;
                    }
                    answer.add(jsonObject);
                    if (parent != null && hasListInValue(answer, parent)) {
                        JsonObject o = valueAsList(answer, parent);
                        for (JsonObject j : o.children) {
                            j.parent = parent;
                            answer.add(j);
                        }
                        answer.remove(o);
                    }
                }
            }
            if (parent != null && !parent.hasValueAsList) {
                List<JsonObject> correct = new ArrayList<>();
                for (JsonObject j : answer) {
                    if (containsAnalog(j.name, answer)) {
                        correct.add(j);
                    }
                }
                for (JsonObject j : correct) {
                    if (answer.contains(j)) {
                        answer.remove(j);
                    }
                }
            }

        }
        //System.out.println(answer);
        return answer;
    }

    public int howManyBracesInTheEnd(String str) {
        char[] charArray = str.toCharArray();
        int sum = 0;
        for (int i = charArray.length - 1; i >= 0; i--) {
            if (charArray[i] == '}' || charArray[i] == ']') {
                sum++;
            } else {
                break;
            }
        }
        return sum;
    }

    public boolean isContentArray(String content) {
        if (content.substring(0, 1) .equals("[")) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean hasChildren(JsonObject jsonObject) {
        //System.out.println(jsonObject.content);
        return jsonObject.content.substring(0,1).equals("{") || jsonObject.content.substring(0,1).equals("[");
    }

    public String generateValue(JsonObject jsonObject, List<JsonObject> list) {
        for (JsonObject o : list) {
            if (o.name.length() > 1 && o.name.substring(0, 1).equals("#") && !o.content.substring(0, 1).equals("{")) {
                return o.content;
            } else if (o.name.length() == 1 && o.name.substring(0, 1).equals("#")) {
                return "";
            }
        }
        return null;
    }

    public List<String> generateAttributes(JsonObject jsonObject, List<JsonObject> list) {
        List<String> attributes = new ArrayList<>();
        for (JsonObject o : list) {
            if (o.name.length() > 0 && o.name.substring(0, 1).equals("@")) {
                if (o.value == null || o.value.equals("null")) {
                    attributes.add(o.name.substring(1) + " = \"\"");
                } else if (o.value.contains("\"")) {
                    attributes.add(o.name.substring(1) + " = " + o.value);
                } else {
                    attributes.add(o.name.substring(1) + " = \"" + o.value + "\"");
                }
            }
        }
        return attributes;
    }

    public boolean hasListInValue(List<JsonObject> list, JsonObject jsonObject) {
        for (JsonObject o : list) {
            if (o.name.equals("#" + jsonObject.name)) {
                if (o.content.substring(0, 1).equals("{") || o.content.substring(0, 1).equals("[")) {
                    jsonObject.hasValueAsList = true;
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public JsonObject valueAsList(List<JsonObject> list, JsonObject jsonObject) {
        for (JsonObject o : list) {
            if (o.name.equals("#" + jsonObject.name)) {
                return o;
            }
        }
        return null;
    }

    public boolean containsAnalog(String name, List<JsonObject> list) {
        if (name.contains("#") || name.contains("@")) {
            for (JsonObject j : list) {
                if (j.name.equals(name.substring(1))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void generateAllJsonObject(List<JsonObject> list, JsonObject parent) {
        //System.out.println(parent.name + " " + parent.content + " " + parent.children);
        if (parent.name.length() > 0 && !parent.name.substring(0, 1).equals("#") && !parent.name.substring(0, 1).equals("@")) {
            //System.out.println("if (parent.name.length() > 0 && !parent.name.substring(0, 1).equals(\"#\") && !parent.name.substring(0, 1).equals(\"@\"))");
            if (errorObjects(parent, parent.children).size() > 0) {
                //System.out.println("(errorObjects(parent, parent.children).size() > 0)");
                list.addAll(errorObjects(parent, parent.children));
                parent.value = null;
                parent.attributes = null;
            }
            list.add(parent);
            if (errorObjects(parent, parent.children).size() > 0) {
                //System.out.println("(errorObjects(parent, parent.children).size() > 0)");
                list.addAll(errorObjects(parent, parent.children));
            }
            if (hasChildren(parent)) {
                //System.out.println("(hasChildren(parent)) ");
                for (JsonObject o : parent.children) {
                    generateAllJsonObject(list, o);
                }
            }
        }
    }







    public List<JsonObject> errorObjects(JsonObject parent, List<JsonObject> children) {
        List<JsonObject> answer = new ArrayList<>();
        if (!parent.hasValueAsList && ((!hasValue(children) && hasAttribute(children)|| hasInCorrectValue(parent.name, children) || hasSomething(parent.name, children) || hasArray(parent.content)))) {
            for (JsonObject j : children) {
                if (j.name.substring(0, 1).equals("#") || j.name.substring(0, 1).equals("@")) {
                    j.name = j.name.substring(1);
                    answer.add(j);
                }
            }
        }
        return answer;
    }

    public boolean hasValue(List<JsonObject> children) {
        boolean answer = false;
        for (JsonObject o : children) {
            if (o.name.contains("#")) {
                answer = true;
                break;
            }
        }
        return answer;
    }

    public boolean hasAttribute(List<JsonObject> children) {
        boolean answer = false;
        for (JsonObject o : children) {
            if (o.name.contains("@")) {
                answer = true;
                break;
            }
        }
        return answer;
    }

    public boolean hasInCorrectValue(String name, List<JsonObject> children) {
        boolean answer = false;
        for (JsonObject o : children) {
            if (o.name.contains("#") && !o.name.contains("#" + name)) {
                answer = true;
                break;
            }
        }
        return answer;
    }

    public boolean hasSomething(String name, List<JsonObject> children) {
        boolean answer = false;
        if (hasValue(children)) {
            for (JsonObject o : children) {
                if (!o.name.substring(0, 1).equals("#") && !o.name.substring(0, 1).equals("@")) {
                    answer = true;
                    break;
                }
            }
        }
        return answer;
    }

    public boolean hasArray(String content) {
        return content.substring(0, 1).equals("[");
    }


}



/*

public void generateAllJsonObject(List<JsonObject> list, JsonObject parent) {
        if (parent.name.length() > 0 && !parent.name.substring(0, 1).equals("#") && !parent.name.substring(0, 1).equals("@")) {
            if (errorObjects(parent, parent.children).size() > 0) {
                list.addAll(errorObjects(parent, parent.children));
                parent.value = null;
                parent.attributes = null;
            }
            list.add(parent);
            if (errorObjects(parent, parent.children).size() > 0) {
                list.addAll(errorObjects(parent, parent.children));
            }
            if (hasChildren(parent)) {
                for (JsonObject o : parent.children) {
                    generateAllJsonObject(list, o);
                }
            }
        }
    }







    public List<JsonObject> errorObjects(JsonObject parent, List<JsonObject> children) {
        List<JsonObject> answer = new ArrayList<>();
        if (!parent.hasValueAsList && ((!hasValue(children) && hasAttribute(children)|| hasInCorrectValue(parent.name, children) || hasSomething(parent.name, children)))) {
            for (JsonObject j : children) {
                if (j.name.substring(0, 1).equals("#") || j.name.substring(0, 1).equals("@")) {
                    j.name = j.name.substring(1);
                    answer.add(j);
                }
            }
        }
        return answer;
    }

    public boolean hasValue(List<JsonObject> children) {
        boolean answer = false;
        for (JsonObject o : children) {
            if (o.name.contains("#")) {
                answer = true;
                break;
            }
        }
        return answer;
    }

    public boolean hasAttribute(List<JsonObject> children) {
        boolean answer = false;
        for (JsonObject o : children) {
            if (o.name.contains("@")) {
                answer = true;
                break;
            }
        }
        return answer;
    }

    public boolean hasInCorrectValue(String name, List<JsonObject> children) {
        boolean answer = false;
        for (JsonObject o : children) {
            if (o.name.contains("#") && !o.name.contains("#" + name)) {
                answer = true;
                break;
            }
        }
        return answer;
    }

    public boolean hasSomething(String name, List<JsonObject> children) {
        boolean answer = false;
        if (hasValue(children)) {
            for (JsonObject o : children) {
                if (!o.name.substring(0, 1).equals("#") && !o.name.substring(0, 1).equals("@")) {
                    answer = true;
                    break;
                }
            }
        }
        return answer;
    }
*/






























