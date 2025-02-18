FROM tomcat
WORKDIR /blog
COPY . .
RUN tr -d '\015' <gradlew >gradlew_temp
RUN rm gradlew
RUN mv gradlew_temp gradlew
RUN bash gradlew war
COPY build/libs/blog.war /usr/local/tomcat/webapps