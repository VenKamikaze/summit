# Summit Project Development Guidelines

## Project Overview
This is a standard Spring Framework based Java web application utilizing Maven to build it. It has been migrated from older Spring Framework 3.x to a newer Spring-Boot 3.3.x base.
This web application is a reimagining of an Oracle Rapid Application Development tool called 'Application Express'. It uses a data driven design to hold template information, show pages, reports, forms, fields, conditional display, validation and processing. Its primary support target for a database is PostgreSQL, however it should be agnostic enough to also run on Oracle (limited effort has been made here) or MSSQL (no effort has been made here) or other databases.

## 1. Build and Configuration

### Project Specifications
- **Java Version**: 17.
- **Build System**: Maven.
- **Database**: PostgreSQL (primary), H2 (testing).

### Build Instructions
To build the project and run tests, use:
```bash
mvn clean install
```
*Note: If you encounter issues with broken symbolic links in `src/main/resources/static`, you may need to skip resource processing or fix the links.*

### Configuration
- Core settings are in `src/main/resources/application.properties` and `application.yaml`.
- Security and CSRF are managed via Spring Security.

## 2. Testing Information

### Configuring and Running Tests
- **JUnit 5** and **Spring Boot Test** are used.
- **H2** provides an in-memory database for testing (configured in `pom.xml`).

To run a specific test class:
```bash
mvn test -Dtest=StringUtilsTest
```

### Adding New Tests
- Place tests in `src/test/java` following the package structure.
- For data-driven logic, verify SQL result mapping using `SourceProcessorResultTable`.

### Simple Test Example
Unit test for `StringUtils` (handles page parameter parsing):
```java
package org.awiki.kamikaze.summit.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {
    @Test
    void testToParameterMap() {
        String pageParams = "key1:value1,key2:value2";
        MultiValueMap<String, String> result = StringUtils.toParameterMap(pageParams);
        assertEquals(2, result.size());
        assertEquals("value1", result.getFirst("key1"));
    }
}
```

## 3. Data-Driven Architecture

### Design Philosophy
Summit uses a metadata-driven approach where the application structure is defined in the database.
- **SQL Source**: Content often derives from SQL queries stored in the `SOURCE` table.
- **Processors**: `SourceProcessorService` implementations execute queries and transform results.
- **Result Mapping**: `SourceProcessorResultTable` and `SourceProcessorResultTableExtractor` map `ResultSet` data into generic structures for rendering.

### Development Workflow
- When modifying Java source files, synchronize changes with existing SQL files in `src/main/resources/sql/postgres/summitdev/` to ensure page stability.
- Carefully examine these SQL files to understand how pages are built from the data-driven design.

### Restrictions for data driven content generation
- When generating SQL for new pages and/or content, please restrict your ID range to between -40000 and -20000
- Modifying existing pages can and should keep existing ids.

### Default templates in data driven content generation
- When creating new pages/regions/fields, please default to using the following templates unless there's a reason not to:
  - Page, template ID: -100
  - Report Region, template ID: -200
  - Field item hidden report td href link, template ID: -50
  - Field item Hidden, template ID: -60
  - Field item Text, template ID: -61
  - Field item Number, template ID: -62
  - Field item DropDown, template ID: -63
  - DropDown Option Item, template ID: -1063
  - Label, template ID: -1000
  - Field item - Submit Button, template ID: -80
  - Field item - Javascript Button, template ID: -81
  - Field item - Javascript Submit Button, template ID: -82

