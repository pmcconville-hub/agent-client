/*
 * Copyright 2024 Spring AI Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springaicommunity.agents.samples.createfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Multi-provider sample demonstrating the simplest AgentClient task: creating a file.
 *
 * <p>
 * Run with different providers via Spring profiles:
 * <pre>
 * mvn spring-boot:run                                    # Claude (default)
 * mvn spring-boot:run -Dspring.profiles.active=codex     # Codex
 * mvn spring-boot:run -Dspring.profiles.active=gemini    # Gemini
 * </pre>
 *
 * @author Spring AI Community
 */
@SpringBootApplication
public class CreateFileApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreateFileApplication.class, args);
	}

}
