package com.ouc.aamanagement.entity;

import lombok.Data;

@Data
public class AdmissionTemplate {
    private String background;
    private TemplateElement title;
    private TemplateElement content;
    private TemplateElement footer;

    @Data
    public static class TemplateElement {
        private String text;
        private String color;
        private Integer fontSize;
        private String fontFamily;
        private String fontWeight;
    }
} 