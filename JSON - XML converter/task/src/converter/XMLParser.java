package converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLParser {
    Map<String, Integer> mapOfEqualsElement = new HashMap<>();
    //int count = 0;

    public Element createRootElement(String xml){
        //System.out.println(xml);
        Element element = new Element();
        element.isRoot = true;
        element.tagName = getTagName(getOpeningTag(xml));
        element.content = getContentOfElement(xml);
        element.attributes = getAttributes(getOpeningTag(xml));
        element.children = getChildren(getContentOfElement(xml), element);
        element.value = getValue(xml);
       // System.out.println();
        return element;
    }

    public Element createElement(String xml, Element parent) {
        Element element = new Element();
        element.isRoot = false;
        element.tagName = getTagName(getOpeningTag(xml));
        element.content = getContentOfElement(xml);
        element.attributes = getAttributes(getOpeningTag(xml));
        element.parent = parent;
        element.children = getChildren(getContentOfElement(xml), element);
        element.value = getValue(xml);
        return element;
    }

    public String prepareXML(String xml) {
        return xml.replaceAll("\\s*=\\s*", "=").
                replaceAll("\n*", "").
                replaceAll(">\\s*<", "><").
                replaceAll("<\\s*", "<").replaceAll("\\s*>", ">").
                replaceAll("<\\?.*\\?>", "").
                replaceAll("'", "\"");
    }

    public String getOpeningTag(String xml) {
        //System.out.println("getOpeningTag = " + xml);
        return xml.substring(xml.indexOf("<"), xml.indexOf(">") + 1);
    }

    public String getTagName(String tag) {
        if (tag.contains(" ")) {
            String[] b = tag.split(" ");
            return b[0].substring(1);
        } else {
            if (withoutClosingTag(tag)) {
                return tag.substring(1, tag.length() - 2);
            } else {
                return tag.substring(1, tag.length() - 1);
            }
        }
    }

    public String getClosingTag(String tag) {
        if (tag.substring(tag.length() - 2, tag.length() - 1).equals("/")) {
            return null;
        } else {
            return "</" + getTagName(tag) + ">";
        }
    }

    public String getContentOfElement(String xml) {
       // System.out.println("xml = " + xml);
        String openingTag = getOpeningTag(xml);
        String closingTag = getClosingTag(openingTag);
       // System.out.println("openingTag = " + openingTag);
      //  System.out.println("closingTag = " + closingTag);
        if (closingTag == null) {
            return null;
        } else {
            String answer = xml.substring(xml.indexOf(openingTag) + openingTag.length(), indexOfClosingTag(xml, openingTag, closingTag));
            //System.out.println("Answer = " + answer);
            return answer;
        }
    }
            /*
        } else if (xml.substring(xml.indexOf(openingTag) + openingTag.length(), xml.indexOf(closingTag)).contains(openingTag)) {
            System.out.println("content first= " + getContentOfElementWithEqualElements(xml, openingTag, closingTag));
            return getContentOfElementWithEqualElements(xml, openingTag, closingTag);
        } else {
            System.out.println("content second= " + xml.substring(xml.indexOf(openingTag) + openingTag.length(), xml.indexOf(closingTag)));
            return xml.substring(xml.indexOf(openingTag) + openingTag.length(), xml.indexOf(closingTag));
        }
    }*/

    public int indexOfClosingTag(String xml, String openingTag, String closingTag) {
        int open = 0;
        int close = 0;
        int index = 0;
        //int max = Math.max(openingTag.length(), closingTag.length());
        for (int i = 0; i < xml.length() - closingTag.length() + 1; i++) {
           // System.out.println(xml.substring(i, i + openingTag.length()));
         //   System.out.println(xml.substring(i, i + closingTag.length()));
            if ((i + openingTag.length()) <= xml.length() && xml.substring(i, i + openingTag.length()).equals(openingTag)) {
                open++;

            }
            if (xml.substring(i, i + closingTag.length()).equals(closingTag)) {
                close++;
            }
            //System.out.println("open = " + open);
            //System.out.println("close = " + close);
           // System.out.println("i = " + i);
            if (open == close) {
                index = i;
                break;
            }
        }
        //System.out.println(index);
        return index;
    }
 /*   public void howManyEqualsTag(String xml, String tag) {
        Map<String, Integer> answer= new HashMap<>();
        int offset = 0;
        int a = 0;
        while (offset != xml.length()) {
            if (xml.contains(tag))
        }

    }*/

    public String getContentOfElementWithEqualElements(String xml, String openingTag, String closingTag) {
        //System.out.println("getContentOfElementWithEqualElements");
       // System.out.println("xml = " + xml);
      //  System.out.println("openingTag = " + openingTag);
      //  System.out.println("closingTag = " + closingTag);
        int count;
        if (mapOfEqualsElement.containsKey(openingTag)) {
            count = mapOfEqualsElement.get(openingTag);
            count++;
        } else {
            count = 1;
        }
        mapOfEqualsElement.put(openingTag, count);
        int i = count;
        int begin = firstNumberOfTag(xml, openingTag, i);
        int end = indexOfClosingTag(xml, openingTag, closingTag);
     //   System.out.println("befin = " + begin);
      //  System.out.println("end = " + end);
     //   System.out.println("answer = " + xml.substring(begin + openingTag.length(), end));
        return xml.substring(begin + openingTag.length(), end);
    }

    public int firstNumberOfTag(String xml, String openingTag, int order) {
        int answer = 0;
        int i = 0;
        while (order != 0) {
            if (xml.contains(openingTag)) {
                answer = xml.indexOf(openingTag, i);
                i = answer + openingTag.length();
                order--;
            }
        }
        return answer;
    }

    public int lastNumberOfTag(String xml, String closingTag, int order) {
        int answer = 0;
        int i = xml.length() - 1;
        while (order != 0) {
            if (xml.substring(0, i).contains(closingTag)) {
                answer = xml.substring(0, i).lastIndexOf(closingTag);
                i = answer;
                order--;
            }
        }
        return answer;
    }

    public String getValue(String xml) {
        //System.out.println("getValue(String xml)");
        String openingTag = getOpeningTag(xml);
        String closingTag = getClosingTag(openingTag);
        String content = getContentOfElement(xml);
        if (closingTag == null) {
            return null;
        } else if (isContentValue(content)){
            return xml.substring(xml.indexOf(openingTag) + openingTag.length(), xml.indexOf(closingTag));
        } else if (content.length() == 0){
            return "";
        } else return "*";
    }

    public boolean withoutClosingTag(String tag) {
        return tag.substring(tag.length() - 2, tag.length() - 1).equals("/");
    }

    public List<String> getStringChildren(String contentOfElement) {
        List<String> children = new ArrayList<>();
        //System.out.println("contentOfElement.length() = " + contentOfElement.length());
        int begin = 0;
        while (begin != contentOfElement.length()) {
            //System.out.println("begin = " + begin);
            String el = getElement(contentOfElement, begin);
            children.add(el);
            begin += el.length();
        }
        return children;
    }

    public String getElement(String xml, int begin) {
        String area = xml.substring(begin);
        //System.out.println("getElement(String xml, int begin) " + begin);
        String openingTag = getOpeningTag(area);
        String contentOfElement = getContentOfElement(area);
        if (contentOfElement == null) {
            contentOfElement = "";
        }
        String closingTag = getClosingTag(openingTag);
        if (closingTag == null) {
            closingTag = "";
        }
        String answer = openingTag + contentOfElement + closingTag;
       // System.out.println("answer = " + answer);
        return answer;

    }

    public boolean hasAttributes(String tag) {
        return tag.contains("=");
    }

    public String getTagContent(String tag) {
        if (withoutClosingTag(tag)) {
            return tag.substring(1, tag.length() - 2);
        } else {
            return tag.substring(1, tag.length() - 1);
        }
    }

    public String[] getAttributes(String tag) {
        String tagContent = getTagContent(tag);
        String[] s = tagContent.split(" ");
        String[] answer = new String[s.length - 1];
        for (int i = 0; i < answer.length; i++) {
            answer[i] = s[i + 1];
        }
        return answer;
    }

    public boolean isContentValue(String content) {
        if (content == null || content.equals("")) {
            return true;
        }
            return !content.substring(0, 1).equals("<");
        }

    public List<Element> getChildren(String contentOfElement, Element parent) {
      //  System.out.println(parent + " " + contentOfElement);
        if (isContentValue(contentOfElement)) {
            return null;
        } else {
            List<Element> children = new ArrayList<>();
            for (String s : getStringChildren(contentOfElement)) {
                children.add(createElement(s, parent));
            }
            return children;
        }
    }

    public void getAllElements(List<Element> list, Element element) {
        list.add(element);
        //System.out.println(element +" " + element.value + " " + element.attributes.length + " " + element.children);
        if (element.hasChildren()) {
            for (Element e : element.children) {
                getAllElements(list, e);
            }
        }
    }

}
