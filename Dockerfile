FROM openjdk:21

COPY . .

RUN ./gradlew --no-daemon build

EXPOSE 7070

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar