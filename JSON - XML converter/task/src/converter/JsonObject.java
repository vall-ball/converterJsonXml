package converter;

import java.util.ArrayList;
import java.util.List;

public class JsonObject {

    String name;
    String content;
    JsonObject parent;
    List<JsonObject> children = new ArrayList<>();
    String value;
    List<String> attributes = new ArrayList<>();
    boolean hasValueAsList = false;
    boolean isElementOfArray;

    public List<JsonObject> path() {
        JsonObject j = this;
        List<JsonObject> path = new ArrayList<>();
        path.add(j);
        while (j.parent != null) {
            j = j.parent;
            path.add(j);
        }
        return path;
    }

    public String pathToString() {
        List<JsonObject> path = path();
        StringBuilder builder = new StringBuilder();
        for (int i = path.size() - 1; i >= 0; i--) {
            builder.append(path.get(i)).append(", ");
        }
        String answer = builder.toString();
        return answer.substring(0, answer.length() - 2);
    }

    @Override
    public String toString() {
        return name;
    }

    public String info() {
        StringBuilder attr = new StringBuilder();
        attr.append("Element:\n").append("path = " + pathToString() + "\n");
        if (value != null) {
            if (value.equals("null")) {
                attr.append("value = null\n");
            } else if (!value.contains("\"")) {
                attr.append("value = \"" + value + "\"\n");
            } else {
                attr.append("value = " + value + "\n");
            }

            if (attributes.size() != 0) {
                attr.append("attributes:\n");
                for (String s : attributes) {
                    attr.append(s + "\n");
                }
            }
        } else if (children.size() == 0) {
            attr.append("value = \"\"\n");
        }
        if (hasValueAsList) {
            if (attributes.size() != 0) {
                attr.append("attributes:\n");
                for (String s : attributes) {
                    attr.append(s + "\n");
                }
            }
        }
        return attr.toString();
    }

    public String format() {
        StringBuilder builder = new StringBuilder();
        if (!hasArray()) {
            if (!isElementOfArray) {
                builder.append("\"" + name + "\":");
            }
            if (children.size() != 0) {
                builder.append("{");
                for (int i = 0; i < children.size(); i++) {
                    builder.append(children.get(i).format());
                    if (i != children.size() - 1) {
                        builder.append(",");
                    }
                }
                builder.append("}");
            } else {
                if (value != null && !value.equals("null") && ((value.length() > 0 && !value.substring(0, 1).equals("\"")) || value.length() == 0)) {
                    builder.append("\"" + value + "\"");
                } else {
                    builder.append(value);
                }
            }
            return builder.toString();
        } else {
            if (!isElementOfArray) {
                builder.append("\"" + name + "\":");
            }
            if (children.size() != 0) {
                builder.append("[");
                for (int i = 0; i < children.size(); i++) {
                    children.get(i).isElementOfArray = true;
                    builder.append(children.get(i).format());
                    if (i != children.size() - 1) {
                        builder.append(",");
                    }
                }
                builder.append("]");
            }
            return builder.toString();
        }
    }

    public String formatElOfArray() {
        return "";
    }

    public boolean hasArray() {
        if (children != null && children.size() != 0) {
            for (int i = 0; i < children.size(); i++) {
                for (int j = 0; j < children.size(); j++) {
                    if (children.get(i).name.equals(children.get(j).name) && i != j) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
