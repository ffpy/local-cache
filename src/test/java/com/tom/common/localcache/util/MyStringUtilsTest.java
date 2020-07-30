package com.tom.common.localcache.util;

import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyStringUtilsTest {

    @Test
    void kebabCaseToCamelCaseTest() {
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase(null))
                .isNull();
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase(""))
                .isEqualTo("");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase(" "))
                .isEqualTo(" ");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("hello-world"))
                .isEqualTo("helloWorld");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("hello--world"))
                .isEqualTo("helloWorld");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("hello-world-"))
                .isEqualTo("helloWorld");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("hello-World"))
                .isEqualTo("helloWorld");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("Hello-world"))
                .isEqualTo("helloWorld");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("-hello-world-"))
                .isEqualTo("HelloWorld");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("hello-world-hi"))
                .isEqualTo("helloWorldHi");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("a--b-c"))
                .isEqualTo("aBC");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("hello-world-1sc"))
                .isEqualTo("helloWorld1sc");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("helloWorld"))
                .isEqualTo("helloWorld");
        Assertions.assertThat(MyStringUtils.kebabCaseToCamelCase("HelloWorld"))
                .isEqualTo("helloWorld");
    }
}