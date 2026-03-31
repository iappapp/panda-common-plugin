package com.github.iappapp.panda.generate.utils;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlFormatter {
    /**
     * 格式化 XML 代码
     * @param input 原始 XML 文本
     * @return 格式化后的文本
     */
    public static String format(String input) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // 注意：某些 JDK 环境下可能不支持此属性设置
            try {
                transformerFactory.setAttribute("indent-number", 4);
            } catch (Exception e) {
                // 忽略不支持的情况
            }
            
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            
            transformer.transform(xmlInput, new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception e) {
            System.err.println("XML 格式化失败: " + e.getMessage());
            return input;
        }
    }
}