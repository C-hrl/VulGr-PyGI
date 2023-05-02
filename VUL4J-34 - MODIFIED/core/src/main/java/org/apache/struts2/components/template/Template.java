

package org.apache.struts2.components.template;

import java.util.ArrayList;
import java.util.List;


public class Template implements Cloneable {
    String dir;
    String theme;
    String name;

    
    public Template(String dir, String theme, String name) {
        this.dir = dir;
        this.theme = theme;
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public String getTheme() {
        return theme;
    }

    public String getName() {
        return name;
    }

    public List<Template> getPossibleTemplates(TemplateEngine engine) {
        List<Template> list = new ArrayList<Template>(3);
        Template template = this;
        String parentTheme;
        list.add(template);
        while ((parentTheme = (String) engine.getThemeProps(template).get("parent")) != null) {
            try {
                template = (Template) template.clone();
                template.theme = parentTheme;
                list.add(template);
            } catch (CloneNotSupportedException e) {
                
            }
        }

        return list;
    }

    
    public String toString() {
        return new StringBuilder().append("/").append(dir).append("/").append(theme).append("/").append(name).toString();
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Template template = (Template) o;

        if (dir != null ? !dir.equals(template.dir) : template.dir != null) return false;
        if (name != null ? !name.equals(template.name) : template.name != null) return false;
        if (theme != null ? !theme.equals(template.theme) : template.theme != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dir != null ? dir.hashCode() : 0;
        result = 31 * result + (theme != null ? theme.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

}
