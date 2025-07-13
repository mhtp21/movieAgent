# movieAgent

A demo application showcasing a Multi-Component Processing (MCP) server integration with Spring AI (OpenAI), built on a reactive Spring Boot WebFlux stack.

---

## Table of Contents

1. [Overview](#overview)  
2. [Features](#features)  
3. [Architecture](#architecture)  
4. [Prerequisites](#prerequisites)  
5. [Installation](#installation)  
6. [Configuration](#configuration)  
7. [Running the Application](#running-the-application)  
8. [API Documentation](#api-documentation)  
9. [Project Structure](#project-structure)  
10. [Contributing](#contributing)  
11. [License](#license)  

---

## Overview

**movieAgent** is a proof-of-concept service that integrates:

- **Spring AI**: Simplified OpenAI API usage via `spring-ai-openai-spring-boot-starter`  
- **MCP Server**: A multi-component processing orchestration layer  
- **Reactive Programming**: Non-blocking I/O with Spring WebFlux and Reactor  
- **Resilience4j**: Fault-tolerance and retries  
- **Springdoc**: Auto-generated OpenAPI (Swagger) documentation  

---

## Features

- Chat-style AI interactions driven by OpenAI’s GPT models  
- Dynamic prompt loading through a customizable `PromptEngine`  
- Endpoints built on Spring WebFlux for high concurrency  
- Circuit breakers and rate limiters via Resilience4j  
- Automatic API docs and interactive UI with Springdoc OpenAPI  

---

## Architecture

```
┌───────────────────┐       ┌────────────────────┐
│  REST Controller  │──────▶│  ChatService       │
└───────────────────┘       └────────────────────┘
         │                            │
         ▼                            ▼
┌───────────────────┐       ┌────────────────────┐
│ PromptEngine      │──────▶│ Spring AI (OpenAI) │
└───────────────────┘       └────────────────────┘
         ▲
         │
┌───────────────────┐
│  MCP Server       │
│ (multi-component) │
└───────────────────┘
```

1. **Controller** accepts user requests.  
2. **ChatService** applies business logic, resilience policies.  
3. **PromptEngine** loads and templats prompt definitions (YAML).  
4. **Spring AI** interacts with OpenAI chat endpoints.  
5. **MCP Server** orchestrates multi-agent flows.  

---

## Prerequisites

- Java 21 or later  
- Maven 3.6+  
- An OpenAI API key  
- (Optional) Docker, if you wish to containerize  

---

## Installation

```bash
# Clone the repository
git clone https://github.com/mhtp21/movieAgent.git
cd movieAgent

# Build
./mvnw clean package
```

---

## Configuration

Populate `src/main/resources/application.properties` (or `application.yml`) with at least:

```properties
# Application
spring.application.name=movieAgent
server.port=8080

# OpenAI
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-3.5-turbo
spring.ai.openai.chat.options.temperature=0.7

# Swagger / OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

**Tip:** You can also supply `OPENAI_API_KEY` as an environment variable.

---

## Running the Application

```bash
# From project root
./mvnw spring-boot:run
```

Once started, the service will be available at `http://localhost:8080`.

---

## API Documentation

Interactive API docs are exposed at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`  
- OpenAPI JSON: `http://localhost:8080/api-docs`  

Use these to explore available endpoints, models, and try them directly.

---

## Project Structure

```
movieAgent
├─ .mvn/…
├─ src/
│  ├─ main/
│  │  ├─ java/com/mcpdemo/movieAgent/
│  │  │  ├─ ai/
│  │  │  │  ├─ prompt/             # PromptEngine, templates loader
│  │  │  │  ├─ controller/         # REST controllers
│  │  │  │  └─ service/            # Business logic, resilience
│  │  └─ resources/
│  │     ├─ prompts/               # YAML prompt templates
│  │     └─ application.properties
│  └─ test/                        # Unit and integration tests
├─ mvnw*                          # Maven wrapper
└─ pom.xml                        # Project dependencies & plugins
```

---

## Contributing

Contributions are welcome! Please:

1. Fork this repo  
2. Create a feature branch (`git checkout -b feature/XYZ`)  
3. Commit your changes (`git commit -m "Add XYZ"`)  
4. Push to your branch (`git push origin feature/XYZ`)  
5. Open a Pull Request  

---

## License

This project is released under the **MIT License**. See [LICENSE](LICENSE) for details.
