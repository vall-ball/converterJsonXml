package converter;

import java.util.ArrayList;
import java.util.List;

public class Element {
    Element parent;
    boolean isRoot;
    List<Element> children = new ArrayList<>();
    String tagName;
    String content;
    String[] attributes;
    String value;

    public void setTagName(String tag) {
        this.tagName = tag.substring(1, tag.length() - 1);
    }

    @Override
    public String toString() {
        return tagName;
    }

    public String info() {
        StringBuilder attr = new StringBuilder();
        attr.append("Element:\n").append("path = " + pathToString() + "\n");
        if (value == null) {
            attr.append("value = null\n");
        } else if (!isRoot && !value.equals("*")) {
                attr.append("value = \"" + value + "\"\n");
            }
        if (attributes.length != 0) {
            attr.append("attributes:\n");
            for (String s : attrToString()) {
                attr.append(s + "\n");
            }
        }
        return attr.toString();

    }

    public boolean hasChildren() {
        return children != null;
    }

    public Element getParent() {
        return parent;
    }

    public List<Element> path() {
        Element e = this;
        List<Element> path = new ArrayList<>();
        path.add(e);
        while (!e.isRoot) {
            e = e.parent;
            path.add(e);
        }
        return path;
    }

    public String pathToString() {
        List<Element> path = path();
        StringBuilder builder = new StringBuilder();
        for (int i = path.size() - 1; i >= 0; i--) {
            builder.append(path.get(i)).append(", ");
        }
        String answer = builder.toString();
        return answer.substring(0, answer.length() - 2);
    }

    public String[] attrToString() {
        String[] answer = new String[attributes.length];
        for (int i = 0; i < answer.length; i++) {
            String[] a = attributes[i].split("=");
            answer[i] = a[0] + " = " + a[1];
        }
        return answer;
    }

    public String format() {
        StringBuilder builder = new StringBuilder();
        builder.append("<" + tagName);
        if (attributes != null && attributes.length != 0) {
            for (String attribute : attributes) {
                builder.append(" " + attribute);
            }
        }
        if (children != null && children.size() != 0) {
            builder.append(">");
            for (Element el : children) {
                builder.append(el.format());
            }
            builder.append("</" + tagName + ">");
        } else if (value == null || value.equals("null")) {
            builder.append("/>");
        } else {
            builder.append(">" + value.replaceAll("\"", "") + "</" + tagName + ">");
        }
        return builder.toString();
    }
}
