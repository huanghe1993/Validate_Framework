package com.huanghe.constance;

import lombok.Getter;

@Getter
public enum AnnotationCheckType {
    METHOD("method"),
    OBJECT("object");

    private String name;

    AnnotationCheckType(String name) {
        this.name = name;
    }
}
