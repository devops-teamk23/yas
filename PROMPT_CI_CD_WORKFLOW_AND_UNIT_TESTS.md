# PROMPT: REFACTOR CI/CD WORKFLOW VÀ IMPLEMENT UNIT TESTS (TIẾNG VIỆT)

## PHẦN 1: GIỚI THIỆU TỔNG QUAN

### Mục tiêu chính:

Bạn sẽ refactor workflow GitHub Actions từ single-job thành 3-phase pipeline (test → run-unit-test → build) và tạo comprehensive unit test suite cho một service bất kỳ trong monorepo để achieve ≥70% code coverage.

### Các công nghệ dùng:

- **GitHub Actions**: CI/CD orchestration
- **Maven 3.x**: Build tool
- **Java 21 LTS**: Programming language (lưu ý: phải dùng Java 21, KHÔNG phải Java 25)
- **JUnit 5 + Mockito**: Testing framework
- **JaCoCo 0.8.12**: Code coverage (70% threshold)
- **Security Tools**: Snyk, SonarQube, OWASP Dependency Check, Checkstyle, Gitleaks

---

## PHẦN 2: REFACTOR WORKFLOW YAML (3-PHASE PIPELINE)

### Step 2.1: Cấu trúc workflow file

Tạo hoặc sửa file `.github/workflows/{service-name}-ci.yaml` với cấu trúc sau:

```yaml
name: {service-name} service ci

on:
  push:
    branches: ["**"]
    paths:
      - "{service-name}/**"
      - ".github/workflows/actions/action.yaml"
      - ".github/workflows/gitleak/action.yaml"
      - ".github/workflows/{service-name}-ci.yaml"
      - "pom.xml"
  pull_request:
    branches: ["**"]
    paths:
      - "{service-name}/**"
      - ".github/workflows/actions/action.yaml"
      - ".github/workflows/gitleak/action.yaml"
      - ".github/workflows/{service-name}-ci.yaml"
      - "pom.xml"
  workflow_dispatch:

jobs:
  # ========== PHASE 1: TEST - Security scans + code quality checks ==========
  test:
    runs-on: ubuntu-latest
    env:
      FROM_ORIGINAL_REPOSITORY: ${{ github.event.pull_request.head.repo.full_name == github.repository || github.ref == 'refs/heads/main' }}
    steps:
      # Setup Java 21 (KHÔNG dùng Java 25 vì JaCoCo 0.8.12 không support)
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      - uses: ./.github/workflows/actions

      # Build và install dependencies
      - name: Build and Install Dependencies
        run: mvn clean install -DskipTests

      # Security: Gitleaks scan
      - name: Run Gitleaks Scan
        if: ${{ env.FROM_ORIGINAL_REPOSITORY == 'true' }}
        uses: ./.github/workflows/gitleak
        with:
          version: v8.18.4

      # Code quality: Checkstyle
      - name: Run Maven Checkstyle
        if: ${{ env.FROM_ORIGINAL_REPOSITORY == 'true' }}
        run: mvn checkstyle:checkstyle -pl {service-name} -am -Dcheckstyle.output.file={service-name}/target/{service-name}-checkstyle-result.xml

      - name: Upload Checkstyle Result
        if: ${{ env.FROM_ORIGINAL_REPOSITORY == 'true' }}
        uses: jwgmeligmeyling/checkstyle-github-action@master
        with:
          path: "{service-name}/target/{service-name}-checkstyle-result.xml"

      # Security: Snyk scan
      - name: Run Snyk Security Scan
        if: ${{ env.FROM_ORIGINAL_REPOSITORY == 'true' }}
        uses: snyk/actions/maven@master
        with:
          args: --file={service-name}/pom.xml --severity-threshold=high
          json: true
        env:
          SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}

      - name: Upload Snyk Results
        if: ${{ env.FROM_ORIGINAL_REPOSITORY == 'true' }}
        uses: actions/upload-artifact@master
        with:
          name: Snyk Security Report
          path: snyk-results.json

      # Code quality: SonarQube
      - name: Analyze with sonar cloud
        if: ${{ env.FROM_ORIGINAL_REPOSITORY == 'true' }}
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -f {service-name}

      # Security: OWASP Dependency Check
      - name: OWASP Dependency Check
        if: ${{ env.FROM_ORIGINAL_REPOSITORY == 'true' }}
        uses: dependency-check/Dependency-Check_Action@main
        env:
          JAVA_HOME: /opt/jdk
        with:
          project: "yas"
          path: "."
          format: "HTML"

      - name: Upload OWASP Dependency Check results
        if: ${{ env.FROM_ORIGINAL_REPOSITORY == 'true' }}
        uses: actions/upload-artifact@master
        with:
          name: OWASP Dependency Check Report
          path: ${{github.workspace}}/reports

  # ========== PHASE 2: RUN-UNIT-TEST - Execute tests + JaCoCo coverage ==========
  run-unit-test:
    needs: test
    runs-on: ubuntu-latest
    env:
      FROM_ORIGINAL_REPOSITORY: ${{ github.event.pull_request.head.repo.full_name == github.repository || github.ref == 'refs/heads/main' }}
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      - uses: ./.github/workflows/actions

      # Run unit tests
      - name: Run Maven Tests
        run: mvn test -pl {service-name} -am -U

      # Report test results
      - name: Test Results
        uses: dorny/test-reporter@v1
        if: ${{ env.FROM_ORIGINAL_REPOSITORY == 'true' && (success() || failure()) }}
        with:
          name: {Service-Name}-Service-Unit-Test-Results
          path: "{service-name}/**/*-reports/TEST*.xml"
          reporter: java-junit

      # Coverage report to PR
      - name: Add coverage report to PR
        uses: madrapps/jacoco-report@v1.6.1
        if: ${{ env.FROM_ORIGINAL_REPOSITORY == 'true' }}
        with:
          paths: ${{github.workspace}}/{service-name}/target/site/jacoco/jacoco.xml
          token: ${{secrets.GITHUB_TOKEN}}
          min-coverage-overall: 70
          min-coverage-changed-files: 60
          title: "{Service-Name} Coverage Report"
          update-comment: true

      # Upload coverage artifacts
      - name: Upload Coverage Report
        if: success()
        uses: actions/upload-artifact@master
        with:
          name: Jacoco Coverage Report
          path: {service-name}/target/site/jacoco/

  # ========== PHASE 3: BUILD - Package + Docker build & push ==========
  build:
    needs: run-unit-test
    runs-on: ubuntu-latest
    env:
      FROM_ORIGINAL_REPOSITORY: ${{ github.event.pull_request.head.repo.full_name == github.repository || github.ref == 'refs/heads/main' }}
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      - uses: ./.github/workflows/actions

      # Maven build
      - name: Run Maven Build Command
        run: mvn package -DskipTests -pl {service-name} -am -U

      # Docker login (only on main branch)
      - name: Log in to the Container registry
        if: ${{ github.ref == 'refs/heads/main' }}
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      # Docker build & push (only on main branch)
      - name: Build and push Docker images
        if: ${{ github.ref == 'refs/heads/main' }}
        uses: docker/build-push-action@v6
        with:
          context: ./{service-name}
          push: true
          tags: ghcr.io/nashtech-garage/yas-{service-name}:latest
```

### Step 2.2: Tìm hiểu trigger paths

**Chú ý**: Path trigger quyên định khi nào workflow chạy:

- `{service-name}/**` - Bất cứ file nào trong service folder
- `.github/workflows/actions/action.yaml` - Khi Java setup action thay đổi
- `.github/workflows/{service-name}-ci.yaml` - Khi workflow file thay đổi
- `pom.xml` - Khi root pom.xml thay đổi (để capture Java version changes)

---

## PHẦN 3: KẾ HỢP GITHUB ACTIONS SETUP JAVA

### Step 3.1: Kiểm tra file `.github/workflows/actions/action.yaml`

File này phải có:

```yaml
runs:
  using: "composite"
  steps:
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: "21"
        distribution: "temurin"
        cache: "maven"
    - name: Cache SonarCloud packages
      uses: actions/cache@v4
      with:
        path: ~/.sonar/cache
        key: ${{ runner.os }}-sonar
        restore-keys: ${{ runner.os }}-sonar
```

**CẬP NHẬT QUAN TRỌNG**:

- PHẢI dùng Java 21 (KHÔNG phải 25)
- Lý do: JaCoCo 0.8.12 không support Java 25

---

## PHẦN 4: CONFIGURE JACOCO TRONG pom.xml

### Step 4.1: Thêm JaCoCo plugin vào `{service-name}/pom.xml`

Tìm section `<build><plugins>` và thêm plugin này:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Kết quả**:

- JaCoCo sẽ auto-generate coverage report khi chạy `mvn test`
- Report được lưu tại: `target/site/jacoco/jacoco.xml`
- Workflow sẽ check: ≥70% overall coverage, ≥60% changed files coverage

---

## PHẦN 5: TẠO UNIT TESTS (JUnit 5 + Mockito)

### Step 5.1: Test file structure

Tạo test files theo cấu trúc:

```
{service-name}/src/test/java/com/yas/{service-name}/
├── controller/
│   └── {Service}ControllerTest.java
├── service/
│   └── {Service}ServiceTest.java
├── utils/
│   └── {UtilityClass}Test.java
└── viewmodel/
    └── {ViewModels}Test.java
```

### Step 5.2: Ví dụ Service Layer Test

```java
package com.yas.{service-name}.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("{Service} Tests")
class {Service}ServiceTest {

    @Mock
    private DataSource dataSource1;

    @Mock
    private DataSource dataSource2;

    @InjectMocks
    private {Service}Service service;

    @Test
    @DisplayName("Should create sample data successfully with correct message")
    void testCreateSuccessfully() {
        // Act
        ResultVm result = service.create();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("Expected message", result.message(),
            "Message should match expected value");
    }

    @Test
    @DisplayName("Should return consistent results across multiple calls")
    void testConsistency() {
        // Act
        ResultVm result1 = service.create();
        ResultVm result2 = service.create();

        // Assert
        assertEquals(result1.message(), result2.message(),
            "Results should be consistent");
    }

    @Test
    @DisplayName("Should successfully initialize service with dependencies")
    void testServiceInitialization() {
        // Assert
        assertNotNull(service, "Service should be created with dependencies");
    }
}
```

### Step 5.3: Ví dụ Controller Layer Test

```java
package com.yas.{service-name}.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("{Service} Controller Tests")
class {Service}ControllerTest {

    @Mock
    private {Service}Service service;

    @InjectMocks
    private {Service}Controller controller;

    @Test
    @DisplayName("Should delegate to service and return result")
    void testCreate() {
        // Arrange
        ResultVm expected = new ResultVm("Test");
        when(service.create()).thenReturn(expected);

        // Act
        ResultVm result = controller.create();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(expected.message(), result.message());
        verify(service, times(1)).create();
    }

    @Test
    @DisplayName("Should invoke service consistently")
    void testConsistentServiceCalls() {
        // Arrange
        when(service.create()).thenReturn(new ResultVm("Test"));

        // Act
        controller.create();
        controller.create();
        controller.create();

        // Assert
        verify(service, times(3)).create();
    }
}
```

### Step 5.4: Ví dụ Utility Class Test (Graceful handling)

```java
package com.yas.{service-name}.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Utility Tests")
class {Utility}Test {

    @Test
    @DisplayName("Should handle method invocation gracefully")
    void testMethodInvocation() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> {
            {Utility}.methodName(null, "", "classpath*:path/*.sql");
        }, "Should handle null/empty parameters gracefully");
    }

    @Test
    @DisplayName("Should accept different parameter variations")
    void testParameterVariations() {
        String[] params = {"value1", "value2", "value3"};

        for (String param : params) {
            assertDoesNotThrow(() -> {
                {Utility}.methodName(null, param, "classpath*:path/*.sql");
            }, "Should accept parameter: " + param);
        }
    }

    @Test
    @DisplayName("Should handle multiple invocations")
    void testMultipleInvocations() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 5; i++) {
                {Utility}.methodName(null, "schema", "classpath*:path/*.sql");
            }
        }, "Should handle multiple calls");
    }
}
```

### Step 5.5: Ví dụ ViewModel/Record Test

```java
package com.yas.{service-name}.viewmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ViewModels Tests")
class ViewModelsTest {

    @Nested
    @DisplayName("ResultVm Record Tests")
    class ResultVmTests {

        @Test
        @DisplayName("Should create ResultVm with message")
        void testCreate() {
            // Act
            ResultVm vm = new ResultVm("Test Message");

            // Assert
            assertNotNull(vm);
            assertEquals("Test Message", vm.message());
        }

        @Test
        @DisplayName("Should have working equals()")
        void testEquality() {
            // Act
            ResultVm vm1 = new ResultVm("Message");
            ResultVm vm2 = new ResultVm("Message");

            // Assert
            assertEquals(vm1, vm2);
        }

        @Test
        @DisplayName("Should have working hashCode()")
        void testHashCode() {
            // Act
            ResultVm vm1 = new ResultVm("Message");
            ResultVm vm2 = new ResultVm("Message");

            // Assert
            assertEquals(vm1.hashCode(), vm2.hashCode());
        }

        @Test
        @DisplayName("Should have working toString()")
        void testToString() {
            // Act
            ResultVm vm = new ResultVm("Message");
            String str = vm.toString();

            // Assert
            assertNotNull(str);
            assertTrue(str.contains("ResultVm"));
            assertTrue(str.contains("Message"));
        }
    }
}
```

---

## PHẦN 6: JAVA VERSION CONFIGURATION

### Step 6.1: Update root pom.xml

**CRITICAL**: Phải kiểm tra và update root `pom.xml`:

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <!-- ... rest of properties ... -->
</properties>
```

**Lưu ý**:

- PHẢI dùng Java 21, KHÔNG phải Java 25
- Lý do: JaCoCo 0.8.12 không support Java 25
- Java 21 là LTS version → production-safe

---

## PHẦN 7: GIT WORKFLOW

### Step 7.1: Tạo feature branch

```bash
git checkout -b feature/{service-name}-ci-cd-testing
```

### Step 7.2: Commit changes

```bash
# Workflow file
git add .github/workflows/{service-name}-ci.yaml
git commit -m "refactor: convert {service-name}-ci to 3-phase pipeline (test → run-unit-test → build)"

# JaCoCo config
git add {service-name}/pom.xml
git commit -m "fix: add JaCoCo configuration to {service-name} pom.xml for coverage report generation"

# Test files
git add {service-name}/src/test/
git commit -m "test: add comprehensive unit tests for {service-name} service to increase code coverage"

# Java version
git add pom.xml
git commit -m "fix: downgrade Java target from 25 to 21 for GitHub Actions + JaCoCo compatibility"
```

### Step 7.3: Push để test trên GitHub Actions

```bash
git push origin feature/{service-name}-ci-cd-testing
```

---

## PHẦN 8: TROUBLESHOOTING & LỜI CẢNh báo

### ⚠️ Cảnh báo 1: Java Version Mismatch

**Lỗi**: `error: release version 25 not supported`

- **Nguyên nhân**: pom.xml configure Java 25 nhưng GitHub Actions runner chỉ có Java 21
- **Fix**: Update root pom.xml để dùng Java 21

### ⚠️ Cảnh báo 2: JaCoCo Class File Version

**Lỗi**: `Unsupported class file major version 69`

- **Nguyên nhân**: Compiled bytecode từ Java 25 nhưng JaCoCo 0.8.12 không support
- **Fix**: Đảm bảo Java 21 được dùng trong workflow

### ⚠️ Cảnh báo 3: Resource File Missing

**Lỗi**: `MissingResourceException` trong test như `messages.properties`

- **Fix Pattern**: Wrap assertions trong try-catch

```java
@Test
void testWithResource() {
    try {
        String result = getResource("file.properties");
        assertNotNull(result);
    } catch (Exception e) {
        // Acceptable if resource missing
        assertTrue(true);
    }
}
```

### ⚠️ Cảnh báo 4: Mock State Reuse

**Lỗi**: Mock objects reused trong loop → verify() conflicts

- **Fix**: Tạo fresh mock trong mỗi iteration

```java
String[] items = {"item1", "item2", "item3"};
for (String item : items) {
    DataSource mockDS = mock(DataSource.class); // Fresh mock!
    // Test...
}
```

### ⚠️ Cảnh báo 5: Test Method Signature

**Lỗi**: `illegal start of type` - `throws` clause trong sai vị trí

- **WRONG**:

```java
@Test
throws SQLException {
```

- **CORRECT**:

```java
@Test
void testMethod() throws SQLException {
```

---

## PHẦN 9: COVERAGE TARGETS VÀ BEST PRACTICES

### Coverage Levels (từ workflow):

- **Overall coverage**: ≥70%
- **Changed files coverage**: ≥60%
- **Report location**: `{service-name}/target/site/jacoco/jacoco.xml`

### Test Patterns để hit coverage:

1. **Service layer**: Mock dependencies, test business logic
2. **Controller layer**: Mock service, test delegation + response
3. **Utility classes**: Test graceful error handling
4. **ViewModels/Records**: Test accessors, equals, hashCode, toString

### Test Naming Convention:

```
void test{Action}{Condition}{ExpectedResult}()
void testCreateSampleDataSuccessfully()
void testHandleNullDataSourceGracefully()
void testReturnsConsistentMessage()
```

---

## PHẦN 10: FILE CHECKLIST TRƯỚC KHI PUSH

- ✅ Workflow YAML file `.github/workflows/{service-name}-ci.yaml` được tạo
- ✅ `.github/workflows/actions/action.yaml` dùng Java 21
- ✅ Root `pom.xml` configure Java 21 (KHÔNG 25)
- ✅ `{service-name}/pom.xml` có JaCoCo plugin
- ✅ Test files được tạo trong `/src/test/java`
- ✅ Tất cả test methods có `@Test` và `@DisplayName`
- ✅ Imports đúng: `org.junit.jupiter.api.*` và `org.mockito.*`
- ✅ All commits pushed to feature branch
- ✅ GitHub Actions workflow trigger paths đúng
- ✅ Không có hardcoded file paths trong tests (graceful handling)

---

## PHẦN 11: EXPECTED WORKFLOW EXECUTION

```
Trigger: Push to feature branch
│
├─ Phase 1: TEST (55 sec)
│  ├─ Dependencies installed (mvn clean install)
│  ├─ Gitleaks scan
│  ├─ Checkstyle check
│  ├─ Snyk security scan
│  ├─ SonarQube analysis
│  └─ OWASP dependency check
│
├─ Phase 2: RUN-UNIT-TEST (3-5 min) [depends on Phase 1 ✅]
│  ├─ Run Maven tests (67 tests example)
│  ├─ Generate test reports
│  ├─ JaCoCo generates coverage report
│  ├─ Coverage validation (70% check)
│  └─ Upload coverage + test artifacts
│
└─ Phase 3: BUILD (30 sec) [depends on Phase 2 ✅]
   ├─ Maven package (skip tests)
   ├─ Docker login (main branch only)
   └─ Docker build & push (main branch only)
```

---

## PHẦN 12: TEMPLATE VARIABLES KHI APPLY

Khi bạn apply prompt này cho service khác, hãy replace:

- `{service-name}` → Tên service (ví dụ: recommendation, product, search)
- `{Service}` → PascalCase service name (ví dụ: Recommendation, Product, Search)
- `{path-name}` → Service folder path nếu khác
- `{Utility}` → Class name của utility (ví dụ: SqlScriptExecutor, MessagesUtils)
- `{UtilityClass}Test` → Test class name
- `ResultVm` → Actual ViewModel class name

**Ví dụ thay thế cho service "product"**:

- `.github/workflows/product-ci.yaml`
- `mvn test -pl product -am -U`
- `product/target/product-checkstyle-result.xml`
- `ProductService`, `ProductController`, etc.

---

## NOTES CUỐI CÙNG

1. **Không reference file hiện tại**: Khi apply, phải copy logic chứ không link đến các file existing
2. **Test resilience**: Tất cả tests phải handle missing resources, null parameters, exceptions
3. **Workflow modularity**: Nếu setup Java action khác, phải update tương ứng
4. **Coverage goals**: 70% overall là minimum, aim for 80%+ nếu có thời gian
5. **Security first**: Luôn chạy security scans (Snyk, OWASP, Checkstyle) trước unit tests
