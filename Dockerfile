FROM eclipse-temurin:21-jdk-noble
WORKDIR /blog
COPY . .
CMD ./mvnw clean package && ./target/blog.jar