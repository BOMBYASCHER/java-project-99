FROM gradle:8.5.0-jdk21

COPY . .

RUN gradle --no-daemon bootJar

EXPOSE 7070

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar