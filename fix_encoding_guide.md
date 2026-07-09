# Hướng dẫn sửa lỗi encoding tiếng Việt

## Vấn đề
Bạn đang gặp lỗi encoding tiếng Việt như "H N?i" thay vì "Hà Nội".

## Cách sửa

### 1. Sửa trực tiếp trong file
Tìm và thay thế:
- `H N?i` → `Hà Nội`

### 2. Cấu hình IDE/Editor
1. Mở file trong editor (VS Code, IntelliJ, etc.)
2. Chọn encoding UTF-8
3. Save file với encoding UTF-8

### 3. Cấu hình JSP
Đã thêm vào đầu file:
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
```

### 4. Cấu hình web.xml
Thêm vào web.xml:
```xml
<filter>
    <filter-name>CharacterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
    <init-param>
        <param-name>forceEncoding</param-name>
        <param-value>true</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>CharacterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

### 5. Cấu hình server
- Tomcat: Thêm `-Dfile.encoding=UTF-8` vào JVM options
- Eclipse: Window → Preferences → General → Workspace → Text file encoding → UTF-8

## Kiểm tra
Sau khi sửa, kiểm tra:
1. File được save với encoding UTF-8
2. Browser hiển thị đúng tiếng Việt
3. Không còn ký tự lỗi như "?" hoặc ""
